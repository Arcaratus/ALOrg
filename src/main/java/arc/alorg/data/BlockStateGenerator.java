package arc.alorg.data;

import arc.alorg.ALOrg;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper fileHelper) {
        super(gen, ALOrg.MOD_ID, fileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "ALOrg Blockstates";
    }

    @Override
    protected void registerStatesAndModels() {
        Set<Block> remainingBlocks = Registry.BLOCK.stream()
                .filter(b -> ALOrg.MOD_ID.equals(Registry.BLOCK.getKey(b).getNamespace()))
                .collect(Collectors.toSet());

        remainingBlocks.forEach(this::simpleBlock);
    }
}
