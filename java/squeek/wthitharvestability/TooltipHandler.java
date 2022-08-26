package squeek.wthitharvestability;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import squeek.wthitharvestability.helpers.StringHelper;

@Mod.EventBusSubscriber(modid = ModInfo.MODID, value = Dist.CLIENT)
public class TooltipHandler
{
    public static boolean enableHarvestTooltip;

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event)
    {
        Item item = event.getItemStack().getItem();
        if (item instanceof DiggerItem && enableHarvestTooltip)
        {
            String harvestName = StringHelper.getHarvestLevelName(((DiggerItem) item).getTier());
            event.getToolTip().add(new TranslatableComponent("wailaharvestability.harvestlevel").append(" " + harvestName));
        }
    }
}