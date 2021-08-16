package arc.alorg;

import arc.alorg.client.core.ClientProxy;
import arc.alorg.common.core.IProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ALOrg.MOD_ID)
public class ALOrg {

    public static final String MOD_ID = "alorg";

    public static IProxy proxy = new IProxy() {};

    public ALOrg() {
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        proxy.registerHandlers();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    }
}
