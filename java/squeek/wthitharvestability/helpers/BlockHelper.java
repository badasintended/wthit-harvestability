package squeek.wthitharvestability.helpers;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class BlockHelper
{
	public static final TagKey<Block> SWORD = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge:mineable/sword"));

	private static final HashMap<TagKey<Block>, ItemStack> testTools = new HashMap<>();
	static
	{
		testTools.put(BlockTags.MINEABLE_WITH_PICKAXE, new ItemStack(Items.WOODEN_PICKAXE));
		testTools.put(BlockTags.MINEABLE_WITH_SHOVEL, new ItemStack(Items.WOODEN_SHOVEL));
		testTools.put(BlockTags.MINEABLE_WITH_AXE, new ItemStack(Items.WOODEN_AXE));
		testTools.put(BlockTags.MINEABLE_WITH_HOE, new ItemStack(Items.WOODEN_HOE));
		testTools.put(SWORD, new ItemStack(Items.WOODEN_SWORD));
	}

	public static TagKey<Block> getEffectiveToolOf(Level world, BlockPos blockPos, BlockState state)
	{
		TagKey<Block> effectiveTool = null;
		float hardness = state.getDestroySpeed(world, blockPos);
		if (hardness > 0.0F)
		{
			for (Map.Entry<TagKey<Block>, ItemStack> testToolEntry : testTools.entrySet())
			{
				ItemStack testTool = testToolEntry.getValue();
				if (testTool != null && !testTool.isEmpty() && testTool.getItem() instanceof TieredItem && testTool.getDestroySpeed(state) >= Tiers.WOOD.getSpeed())
				{
					effectiveTool = testToolEntry.getKey();
					break;
				}
			}
		}
		return effectiveTool;
	}

	public static boolean isBlockUnbreakable(Level world, BlockPos blockPos, BlockState state)
	{
		return state.getDestroySpeed(world, blockPos) == -1.0F;
	}

	public static boolean isAdventureModeAndBlockIsUnbreakable(Player player, BlockPos pos)
	{
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
		Level world = player.level;

		return gameType == GameType.SPECTATOR || heldItem.isEmpty() || !heldItem.hasAdventureModeBreakTagForBlock(world.registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY), new BlockInWorld(world, pos, false));
	}

	/**
	 * A copy+paste of ForgeHooks.canHarvestBlock, modified to be position-agnostic
	 * See https://github.com/MinecraftForge/MinecraftForge/pull/2769
	 */
	public static boolean canHarvestBlock(BlockState state, Player player)
	{
		if (!state.requiresCorrectToolForDrops())
		{
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
