package arc.alorg.common.block.tiles;

import arc.alorg.ALOrg;
import arc.alorg.common.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModTiles {

    public static final TileEntityType<TrainingTile> TRAINING = TileEntityType.Builder.of(TrainingTile::new, ModBlocks.TRAINING).build(null);

    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> evt) {
        IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();
        ALOrg.register(r, "training", TRAINING);
    }
}
