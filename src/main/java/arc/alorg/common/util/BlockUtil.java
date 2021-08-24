package arc.alorg.common.util;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtil {
    public static boolean isAir(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.AIR;
    }
}
