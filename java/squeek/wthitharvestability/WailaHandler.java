package squeek.wthitharvestability;

import java.util.ArrayList;
import java.util.List;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.Nullable;
import squeek.wthitharvestability.helpers.BlockHelper;
import squeek.wthitharvestability.helpers.ColorHelper;
import squeek.wthitharvestability.helpers.OreHelper;
import squeek.wthitharvestability.helpers.StringHelper;
import squeek.wthitharvestability.helpers.ToolHelper;
import squeek.wthitharvestability.helpers.ToolType;

@WailaPlugin(id = WailaHarvestability.MOD_ID + ":plugin")
public class WailaHandler implements IBlockComponentProvider, IEventListener, IWailaPlugin {

    @Override
    public @Nullable String getHoveredItemModName(ItemStack stack, IPluginConfig config) {
        TooltipHandler.enableHarvestTooltip = config.getBoolean(LEVEL_ITEM);
        return null;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        ItemStack stack = accessor.getStack();
        Player player = accessor.getPlayer();

        // Disguised (silverfish) blocks have their mimickedBlock given as the stack when Waila's
        // 'hide diguised blocks' option is enabled, so we should get the BlockState from that instead
        if (accessor.getBlock() instanceof InfestedBlock) {
            Block stackBlock = Block.byItem(stack.getItem());
            if (stackBlock != accessor.getBlock())
                state = stackBlock.defaultBlockState();
        }

        boolean minimalLayout = config.getBoolean(MINIMAL);

        List<Component> stringParts = new ArrayList<>();
        try {
            getHarvestability(stringParts, player, state, accessor.getPosition(), config, minimalLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!stringParts.isEmpty()) {
            if (minimalLayout)
                tooltip.addLine(StringHelper.concatenateStringList(stringParts, ChatFormatting.RESET + config.getString(MINIMAL_SEPARATOR)));
            else
                stringParts.forEach(tooltip::addLine);
        }
    }

    public void getHarvestability(List<Component> stringList, Player player, BlockState state, BlockPos pos, IPluginConfig config, boolean minimalLayout) {
        boolean isSneaking = player.isShiftKeyDown();
        boolean showHarvestLevel = config.getBoolean(LEVEL) && (!config.getBoolean(LEVEL_SNEAK) || isSneaking);
        boolean showHarvestLevelNum = config.getBoolean(LEVEL_NUM) && (!config.getBoolean(LEVEL_NUM_SNEAK) || isSneaking);
        boolean showEffectiveTool = config.getBoolean(EFFECTIVE_TOOL) && (!config.getBoolean(EFFECTIVE_TOOL_SNEAK) || isSneaking);
        boolean showCurrentlyHarvestable = config.getBoolean(HARVESTABLE) && (!config.getBoolean(HARVESTABLE_SNEAK) || isSneaking);
        boolean hideWhileHarvestable = config.getBoolean(UNHARVESTABLE_ONLY);
        boolean showOresOnly = config.getBoolean(ORE_ONLY);
        boolean toolRequiredOnly = config.getBoolean(TOOL_REQUIRED_ONLY);

        if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable) {
            if (showOresOnly && !OreHelper.isBlockAnOre(state.getBlock())) {
                return;
            }

            if (BlockHelper.isAdventureModeAndBlockIsUnbreakable(player, pos) || BlockHelper.isBlockUnbreakable(player.level, pos, state)) {
                Component unbreakableString = Component.literal(ColorHelper.getBooleanColor(false)).append(config.getString(HARVESTABLE_FALSE_STRING)).append(" ").append(!minimalLayout ? Component.translatable("waila.h12y.harvestable").withStyle(ChatFormatting.RESET) : Component.literal(""));
                stringList.add(unbreakableString);
                return;
            }

            Tier harvestTier = null;
            for (Tier tier : TierSortingRegistry.getSortedTiers()) {
                if (TierSortingRegistry.isCorrectTierForDrops(tier, state)) {
                    harvestTier = tier;
                    break;
                }
            }
            ToolType effectiveTool = BlockHelper.getEffectiveToolOf(player.level, pos, state);
            if (effectiveTool == null)
                harvestTier = null;

            boolean blockHasEffectiveTools = harvestTier != null;

            String shearability = getShearabilityString(player, state, pos, config);

            if (toolRequiredOnly && !state.requiresCorrectToolForDrops() && !blockHasEffectiveTools && shearability.isEmpty())
                return;

            boolean canHarvest = false;
            boolean isEffective = false;
            boolean isAboveMinHarvestLevel = false;
            boolean isHoldingTinkersTool = false;

            ItemStack heldStack = player.getMainHandItem();
            if (!heldStack.isEmpty()) {
                canHarvest = ToolHelper.canToolHarvestBlock(heldStack, state) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(state, player));
                isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(heldStack, harvestTier);
                isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(heldStack, state, effectiveTool);
            }

            boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel) || (!isHoldingTinkersTool && BlockHelper.canHarvestBlock(state, player));

            if (hideWhileHarvestable && isCurrentlyHarvestable)
                return;

            String currentlyHarvestable = showCurrentlyHarvestable ? ColorHelper.getBooleanColor(isCurrentlyHarvestable) + config.getString(isCurrentlyHarvestable ? HARVESTABLE_TRUE_STRING : HARVESTABLE_FALSE_STRING) + " " + (!minimalLayout ? ChatFormatting.RESET + I18n.get("waila.h12y.currentlyharvestable") : "") : "";

            if (!currentlyHarvestable.isEmpty() || !shearability.isEmpty()) {
                String separator = (!shearability.isEmpty() ? " " : "");
                stringList.add(Component.literal(currentlyHarvestable + separator + shearability));
            }
            if (harvestTier != null && showEffectiveTool) {
                String effectiveToolClass = effectiveTool.toolClass();
                String effectiveToolString;
                if (I18n.exists("waila.h12y.toolclass." + effectiveToolClass)) {
                    effectiveToolString = I18n.get("waila.h12y.toolclass." + effectiveToolClass);
                } else {
                    effectiveToolString = effectiveToolClass.substring(0, 1).toUpperCase() + effectiveToolClass.substring(1);
                }
                stringList.add(Component.translatable(!minimalLayout ? "waila.h12y.effectivetool" : "").append(" ").append(ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + effectiveToolString));
            }
            if (harvestTier != null && (showHarvestLevel || showHarvestLevelNum)) {
                String harvestLevelString = "";
                String harvestLevelName = StringHelper.stripFormatting(StringHelper.getHarvestLevelName(harvestTier));
                String harvestLevelNum = String.valueOf(harvestTier.getLevel());

                // only show harvest level number and name if they are different
                showHarvestLevelNum = showHarvestLevelNum && (!showHarvestLevel || !harvestLevelName.equals(harvestLevelNum));

                if (showHarvestLevel)
                    harvestLevelString = harvestLevelName + (showHarvestLevelNum ? " (" + harvestLevelNum + ")" : "");
                else if (showHarvestLevelNum)
                    harvestLevelString = harvestLevelNum;

                stringList.add(Component.translatable(!minimalLayout ? "waila.h12y.harvestlevel" : "").append(" ").append(ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + harvestLevelString));
            }
        }
    }

    public String getShearabilityString(Player player, BlockState state, BlockPos pos, IPluginConfig config) {
        boolean isSneaking = player.isShiftKeyDown();
        boolean showShearability = config.getBoolean(SHEARABILITY) && (!config.getBoolean(SHEARABILITY_SNEAK) || isSneaking);

        boolean isDoublePlant = state.getBlock() instanceof DoublePlantBlock; //Special case for DoublePlantBlock, as it does not implement IShearable currently
        boolean canBeSheared = state.getBlock() instanceof IForgeShearable || isDoublePlant;
        if (showShearability && canBeSheared) {
            ItemStack heldStack = player.getMainHandItem();
            boolean isHoldingShears = !heldStack.isEmpty() && heldStack.getItem() instanceof ShearsItem;
            boolean isShearable = isHoldingShears && (isDoublePlant || ((IForgeShearable) state.getBlock()).isShearable(heldStack, player.level, pos));
            return ColorHelper.getBooleanColor(isShearable, !isShearable && isHoldingShears) + config.getString(SHEARABILITY_STRING);
        }
        return "";
    }

    private static final ResourceLocation
        LEVEL = rl("level.enabled"),
        LEVEL_ITEM = rl("level.item"),
        LEVEL_SNEAK = rl("level.sneak"),
        LEVEL_NUM = rl("level_num.enabled"),
        LEVEL_NUM_SNEAK = rl("level_num.sneak"),
        HARVESTABLE = rl("harvestable.enabled"),
        HARVESTABLE_TRUE_STRING = rl("harvestable.true_string"),
        HARVESTABLE_FALSE_STRING = rl("harvestable.false_string"),
        HARVESTABLE_SNEAK = rl("harvestable.sneak"),
        EFFECTIVE_TOOL = rl("effective_tool.enabled"),
        EFFECTIVE_TOOL_SNEAK = rl("effective_tool.sneak"),
        SHEARABILITY = rl("shearability.enabled"),
        SHEARABILITY_STRING = rl("shearability.string"),
        SHEARABILITY_SNEAK = rl("shearability.sneak"),
        MINIMAL = rl("minimal.enabled"),
        MINIMAL_SEPARATOR = rl("minimal.separator"),
        ORE_ONLY = rl("ores_only"),
        UNHARVESTABLE_ONLY = rl("unharvestable_only"),
        TOOL_REQUIRED_ONLY = rl("tool_required_only");

    private static ResourceLocation rl(String path) {
        return new ResourceLocation("harvestability", path);
    }

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(MINIMAL, false);
        registrar.addConfig(MINIMAL_SEPARATOR, " : ");
        registrar.addMergedConfig(HARVESTABLE, true);
        registrar.addConfig(HARVESTABLE_TRUE_STRING, "\u2714");
        registrar.addConfig(HARVESTABLE_FALSE_STRING, "\u2718");
        registrar.addConfig(HARVESTABLE_SNEAK, false);
        registrar.addMergedConfig(LEVEL, true);
        registrar.addMergedConfig(LEVEL_ITEM, true);
        registrar.addConfig(LEVEL_SNEAK, false);
        registrar.addConfig(LEVEL_NUM, false);
        registrar.addConfig(LEVEL_NUM_SNEAK, false);
        registrar.addMergedConfig(EFFECTIVE_TOOL, true);
        registrar.addConfig(EFFECTIVE_TOOL_SNEAK, false);
        registrar.addMergedConfig(SHEARABILITY, true);
        registrar.addConfig(SHEARABILITY_STRING, "\u2702");
        registrar.addConfig(SHEARABILITY_SNEAK, false);
        registrar.addConfig(TOOL_REQUIRED_ONLY, true);
        registrar.addConfig(ORE_ONLY, false);
        registrar.addConfig(UNHARVESTABLE_ONLY, false);

        registrar.addComponent(this, TooltipPosition.BODY, Block.class);
        registrar.addEventListener(this);
    }

}