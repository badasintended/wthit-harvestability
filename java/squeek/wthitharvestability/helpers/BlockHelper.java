package squeek.wthitharvestability.helpers;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class BlockHelper {

    public static ToolType getEffectiveToolOf(Level world, BlockPos blockPos, BlockState state) {
        ToolType effectiveTool = null;
        float hardness = state.getDestroySpeed(world, blockPos);
        if (hardness > 0.0F) {
            for (Map.Entry<TagKey<Block>, ToolType> testToolEntry : ToolType.MAP.entrySet()) {
                ItemStack testTool = testToolEntry.getValue().woodenStack();
                if (testTool != null && !testTool.isEmpty() && testTool.getItem() instanceof TieredItem && testTool.getDestroySpeed(state) >= Tiers.WOOD.getSpeed()) {
                    effectiveTool = testToolEntry.getValue();
                    break;
                }
            }
        }
        return effectiveTool;
    }

    public static boolean isBlockUnbreakable(Level world, BlockPos blockPos, BlockState state) {
        return state.getDestroySpeed(world, blockPos) == -1.0F;
    }

    public static boolean isAdventureModeAndBlockIsUnbreakable(Player player, BlockPos pos) {
        ClientPacketListener netHandler = Minecraft.getInstance().getConnection();
        if (netHandler == null)
            return false;

        PlayerInfo networkplayerinfo = netHandler.getPlayerInfo(player.getGameProfile().getId());
        GameType gameType = networkplayerinfo.getGameMode();

        if (gameType != GameType.ADVENTURE)
            return false;

        if (player.mayBuild())
            return false;

        ItemStack heldItem = player.getMainHandItem();
        Level world = player.level();

        return gameType == GameType.SPECTATOR || heldItem.isEmpty() || !heldItem.hasAdventureModeBreakTagForBlock(world.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(world, pos, false));
    }

    /**
     * A copy+paste of ForgeHooks.canHarvestBlock, modified to be position-agnostic
     * See https://github.com/MinecraftForge/MinecraftForge/pull/2769
     */
    public static boolean canHarvestBlock(BlockState state, Player player) {
        if (!state.requiresCorrectToolForDrops()) {
            return true;
        }

        return player.hasCorrectToolForDrops(state);
//
//		ItemStack stack = player.getMainHandItem();
//		ToolType tool = state.getHarvestTool();
//		if (stack.isEmpty() || tool == null)
//		{
//			return player.hasCorrectToolForDrops(state);
//		}
//
//		int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
//		if (toolLevel < 0)
//		{
//			return player.hasCorrectToolForDrops(state);
//		}
//
//		return toolLevel >= state.getHarvestLevel();
    }

}
