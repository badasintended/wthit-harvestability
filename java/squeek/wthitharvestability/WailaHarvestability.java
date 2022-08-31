package squeek.wthitharvestability;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import squeek.wthitharvestability.proxy.TConstructProxy;

@Mod(value = WailaHarvestability.MOD_ID)
public class WailaHarvestability {

    public static final String MOD_ID = "wthitharvestability";

    private static final String TCONSTRUCT = "tconstruct";

    public WailaHarvestability() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        if (ModList.get().isLoaded(TCONSTRUCT)) {
            modBus.addListener(new TConstructProxy());
        }
    }

}