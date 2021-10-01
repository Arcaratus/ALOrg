package arc.alorg.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtil {
    public static boolean isAir(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    /**
     * Fills a solid cube from start to end with replace
     */
    public static void fill(World world, BlockPos start, BlockPos end, BlockState state, boolean replace) {
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            if (replace || isAir(world, pos)) {
                world.setBlock(pos, state, 3);
            }
        }
    }

    public static void fill(World world, BlockPos start, BlockPos end, BlockState state) {
        fill(world, start, end, state, true);
    }

    /**
     * Clears the cuboid shape from start to end
     */
    public static void clear(World world, BlockPos start, BlockPos end) {
        fill(world, start, end, Blocks.AIR.defaultBlockState());
    }

    /**
     * Creates a hollow cube with the given BlockState
     */
    public static void hollowCube(World world, BlockPos start, BlockPos end, BlockState state) {
        fill(world, start, new BlockPos(start.getX(), end.getY(), end.getZ()), state);
        fill(world, start, new BlockPos(end.getX(), start.getY(), end.getZ()), state);
        fill(world, start, new BlockPos(end.getX(), end.getY(), start.getZ()), state);
        fill(world, new BlockPos(end.getX(), start.getY(), start.getZ()), end, state);
        fill(world, new BlockPos(start.getX(), end.getY(), start.getZ()), end, state);
        fill(world, new BlockPos(start.getX(), start.getY(), end.getZ()), end, state);

        BlockPos inner = start.offset(Integer.signum(end.getX() - start.getX()), Integer.signum(end.getY() - start.getY()), Integer.signum(end.getZ() - start.getZ()));
        BlockPos outer = end.offset(Integer.signum(start.getX() - end.getX()), Integer.signum(start.getY() - end.getY()), Integer.signum(start.getZ() - end.getZ()));
        clear(world, inner, outer);
    }
}
