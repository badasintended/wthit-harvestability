package squeek.wthitharvestability.proxy;

import java.util.function.Consumer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.HarvestTiers;
import squeek.wthitharvestability.helpers.StringHelper;
import squeek.wthitharvestability.helpers.ToolHelper;

public class TConstructProxy implements Consumer<FMLCommonSetupEvent> {

    @Override
    public void accept(FMLCommonSetupEvent event) {
        StringHelper.TIER_NAME_GETTERS.add(tier -> HarvestTiers.getName(tier).getString());

        ToolHelper.TIER_GETTERS.add(stack -> stack.getItem() instanceof ModifiableItem modifiable
            ? modifiable.getToolDefinition().getData().getHarvestLogic().getTier(ToolStack.from(stack))
            : null);
    }

}
