package squeek.wthitharvestability.helpers;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

public class ToolHelper {

    public static final List<Function<ItemStack, Tier>> TIER_GETTERS = new ArrayList<>();

    private static final Supplier<Object2IntMap<Tier>> tiers = Suppliers.memoize(() -> {
        Object2IntMap<Tier> map = new Object2IntOpenHashMap<>();
        int i = 0;
        for (Tier tier : TierSortingRegistry.getSortedTiers()) {
            map.put(tier, i);
            i++;
        }
        return map;
    });

    static {
        TIER_GETTERS.add(stack -> stack.getItem() instanceof TieredItem tiered ? tiered.getTier() : null);
    }

    public static boolean isToolEffectiveAgainst(@Nonnull ItemStack tool, BlockState state, ToolType effectiveToolType) {
        return tool.isCorrectToolForDrops(state) || (effectiveToolType != null && tool.canPerformAction(effectiveToolType.action()));
    }

    public static boolean canToolHarvestLevel(@Nonnull ItemStack tool, Tier harvestTier) {
        if (tool.isEmpty()) return false;

        Tier tier = null;
        for (Function<ItemStack, Tier> getter : TIER_GETTERS) {
            tier = getter.apply(tool);
            if (tier != null) break;
        }

        return tier != null && tiers.get().getInt(tier) >= tiers.get().getInt(harvestTier);
    }

    public static boolean canToolHarvestBlock(@Nonnull ItemStack tool, BlockState blockState) {
        return !blockState.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(blockState);
    }

}