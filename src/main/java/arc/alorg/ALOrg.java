package arc.alorg;

import arc.alorg.client.core.ClientProxy;
import arc.alorg.common.core.IProxy;
import arc.alorg.common.entity.ModEntities;
import arc.alorg.data.DataGenerators;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(ALOrg.MOD_ID)
public class ALOrg {

    public static final String MOD_ID = "alorg";

    public static IProxy proxy = new IProxy() {};

    public ALOrg() {
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        proxy.registerHandlers();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(DataGenerators::gatherData);
        modBus.addGenericListener(EntityType.class, ModEntities::registerEntities);
        modBus.addListener(ModEntities::registerAttributes);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> r, String name, IForgeRegistryEntry<V> thing) {
        r.register(thing.setRegistryName(rl(name)));
    }
}
