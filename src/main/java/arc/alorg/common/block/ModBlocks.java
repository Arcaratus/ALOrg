package arc.alorg.common.block;

import arc.alorg.ALOrg;
import arc.alorg.common.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModBlocks {
    private static final AbstractBlock.IExtendedPositionPredicate<EntityType<?>> NO_SPAWN = (state, world, pos, et) -> false;
    private static final AbstractBlock.IPositionPredicate NO_SUFFOCATION = (state, world, pos) -> false;

    public static final Block GOAL = new GoalBlock(Properties.of(Material.METAL).strength(2, 10).sound(SoundType.METAL));
    public static final Block TRAINING = new TrainingBlock(Properties.of(Material.METAL).strength(2, 10).sound(SoundType.METAL));
    public static final Block TRAINING_GLASS = new ALOrgGlassBlock(Properties.copy(Blocks.GLASS).strength(30, 30).noOcclusion().isViewBlocking(NO_SUFFOCATION).isSuffocating(NO_SUFFOCATION).isValidSpawn(NO_SPAWN));

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> r = event.getRegistry();
        ALOrg.register(r, "goal", GOAL);
        ALOrg.register(r, "training", TRAINING);
        ALOrg.register(r, "training_glass", TRAINING_GLASS);
    }

    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();
        Item.Properties props = ModItems.defaultBuilder();
        ALOrg.register(r, Registry.BLOCK.getKey(GOAL), new BlockItem(GOAL, props));
        ALOrg.register(r, Registry.BLOCK.getKey(TRAINING), new BlockItem(TRAINING, props));
        ALOrg.register(r, Registry.BLOCK.getKey(TRAINING_GLASS), new BlockItem(TRAINING_GLASS, props));
    }
}
