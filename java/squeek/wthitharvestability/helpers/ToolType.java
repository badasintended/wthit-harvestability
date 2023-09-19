package squeek.wthitharvestability.helpers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public record ToolType(
    TagKey<Block> tag,
    ItemStack woodenStack,
    ToolAction action,
    String toolClass
) {

    static final Map<TagKey<Block>, ToolType> MAP = new HashMap<>();

    static {
        register(new ToolType(BlockTags.MINEABLE_WITH_PICKAXE, new ItemStack(Items.WOODEN_PICKAXE), ToolActions.PICKAXE_DIG, "pickaxe"));
        register(new ToolType(BlockTags.MINEABLE_WITH_SHOVEL, new ItemStack(Items.WOODEN_SHOVEL), ToolActions.SHOVEL_DIG, "shovel"));
        register(new ToolType(BlockTags.MINEABLE_WITH_AXE, new ItemStack(Items.WOODEN_AXE), ToolActions.AXE_DIG, "axe"));
        register(new ToolType(BlockTags.MINEABLE_WITH_HOE, new ItemStack(Items.WOODEN_HOE), ToolActions.HOE_DIG, "hoe"));
        register(new ToolType(TagKey.create(Registries.BLOCK, new ResourceLocation("forge:mineable/sword")), new ItemStack(Items.WOODEN_SWORD), ToolActions.SWORD_DIG, "sword"));
    }

    public static void register(ToolType toolType) {
        MAP.put(toolType.tag, toolType);
    }

}
