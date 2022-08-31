package squeek.wthitharvestability.helpers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public class OreHelper {

    public static boolean isBlockAnOre(Block block) {
        return isItemAnOre(new ItemStack(block)) || block.defaultBlockState().is(Tags.Blocks.ORES);
    }

    public static boolean isItemAnOre(ItemStack stack) {
        if (stack.is(Tags.Items.ORES)) {
            return true;
        }

        // ore in the display name (but not part of another word)
        if (stack.getDisplayName().getString().matches(".*(^|\\s)([oO]re)($|\\s).*"))
            return true;

        // ore as the start of the unlocalized name
        return stack.getItem().getDescriptionId().startsWith("ore");
    }

}