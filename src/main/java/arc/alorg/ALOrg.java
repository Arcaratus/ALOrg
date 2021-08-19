package arc.alorg;

import arc.alorg.client.core.ClientProxy;
import arc.alorg.common.block.ModBlocks;
import arc.alorg.common.core.IProxy;
import arc.alorg.common.entity.ModEntities;
import arc.alorg.common.item.ModItems;
import arc.alorg.data.DataGenerators;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ALOrg.MOD_ID)
public class ALOrg {

    public static final String MOD_ID = "alorg";

    public static IProxy proxy = new IProxy() {};

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public ALOrg() {
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        proxy.registerHandlers();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(DataGenerators::gatherData);
        modBus.addGenericListener(EntityType.class, ModEntities::registerEntities);
        modBus.addGenericListener(Item.class, ModItems::registerItems);
        modBus.addGenericListener(Block.class, ModBlocks::registerBlocks);
        modBus.addGenericListener(Item.class, ModBlocks::registerItemBlocks);
        modBus.addListener(ModEntities::registerAttributes);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, ResourceLocation name, IForgeRegistryEntry<V> thing) {
        reg.register(thing.setRegistryName(name));
    }

    public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> r, String name, IForgeRegistryEntry<V> thing) {
        register(r, rl(name), thing);
    }
}
