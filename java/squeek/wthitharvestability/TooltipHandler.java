package squeek.wthitharvestability;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import squeek.wthitharvestability.helpers.StringHelper;

@Mod.EventBusSubscriber(modid = WailaHarvestability.MOD_ID, value = Dist.CLIENT)
public class TooltipHandler {

    public static boolean enableHarvestTooltip;

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof DiggerItem && enableHarvestTooltip) {
            String harvestName = StringHelper.getHarvestLevelName(((DiggerItem) item).getTier());
            event.getToolTip().add(Component.translatable("waila.h12y.harvestlevel").append(" " + harvestName));
        }
    }

}