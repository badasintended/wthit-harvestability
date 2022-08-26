package squeek.wthitharvestability.helpers;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class ToolHelper
{
	private static final Map<TagKey<Block>, ToolAction> toolActions = new HashMap<>();
	private static final Map<TagKey<Block>, String> toolClasses = new HashMap<>();
	private static final Supplier<Object2IntMap<Tier>> tiers = Suppliers.memoize(() -> {
		Object2IntMap<Tier> map = new Object2IntOpenHashMap<>();
		int i = 0;
		for (Tier tier : TierSortingRegistry.getSortedTiers()) {
			map.put(tier, i);
			i++;
		}
		return map;
	});

	static
	{
		toolActions.put(BlockTags.MINEABLE_WITH_PICKAXE, ToolActions.PICKAXE_DIG);
		toolActions.put(BlockTags.MINEABLE_WITH_SHOVEL, ToolActions.SHOVEL_DIG);
		toolActions.put(BlockTags.MINEABLE_WITH_AXE, ToolActions.AXE_DIG);
		toolActions.put(BlockTags.MINEABLE_WITH_HOE, ToolActions.HOE_DIG);
		toolActions.put(BlockHelper.SWORD, ToolActions.SWORD_DIG);

		for (TagKey<Block> tag : toolActions.keySet())
		{
			String[] split = tag.location().getPath().split("/");
			toolClasses.put(tag, split[split.length - 1]);
		}
	}

	public static boolean isToolEffectiveAgainst(@Nonnull ItemStack tool, BlockState state, TagKey<Block> effectiveToolType)
	{
		return tool.isCorrectToolForDrops(state) || tool.canPerformAction(toolActions.get(effectiveToolType));
	}

	public static boolean canToolHarvestLevel(@Nonnull ItemStack tool, Tier harvestTier)
	{
		if (tool.isEmpty())
			return false;
		if (tool.getItem() instanceof TieredItem tiered)
			return tiers.get().getInt(tiered.getTier()) >= tiers.get().getInt(harvestTier);
		return false;
	}

	public static boolean canToolHarvestBlock(@Nonnull ItemStack tool, BlockState blockState)
	{
		return !blockState.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(blockState);
	}

	public static String getToolClass(TagKey<Block> tag)
	{
		return toolClasses.get(tag);
	}
}