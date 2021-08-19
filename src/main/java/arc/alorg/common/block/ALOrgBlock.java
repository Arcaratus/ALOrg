package arc.alorg.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ALOrgBlock extends Block {
    public ALOrgBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
        super.triggerEvent(state, world, pos, id, param);
        TileEntity tileentity = world.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(id, param);
    }
}
