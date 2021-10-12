package arc.alorg.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;
import java.util.stream.IntStream;

public class BlockUtil {
    private static final Random random = new Random();

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
     * Creates the unit BlockPos indicating the basic "vector" from start to end
     */
    public static BlockPos unitTo(BlockPos start, BlockPos end) {
        return new BlockPos(Integer.signum(end.getX() - start.getX()), Integer.signum(end.getY() - start.getY()), Integer.signum(end.getZ() - start.getZ()));
    }

    public static BlockPos scale(BlockPos a, int scale) {
        return new BlockPos(a.getX() * scale, a.getY() * scale, a.getZ() * scale);
    }

    /**
     * Returns the "inner" BlockPos of a given selection
     */
    public static Pair<BlockPos, BlockPos> innerPos(BlockPos start, BlockPos end) {
        return Pair.of(start.offset(unitTo(start, end)), end.offset(unitTo(end, start)));
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

        Pair<BlockPos, BlockPos> innerPos = innerPos(start, end);
        clear(world, innerPos.getLeft(), innerPos.getRight());
    }

    /**
     * Fills the selected region randomly with the state
     */
    public static void randomFill(World world, BlockPos start, BlockPos end, BlockState state, boolean replace, double chance) {
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            if (random.nextDouble() < chance) {
                if (replace || isAir(world, pos)) {
                    world.setBlock(pos, state, 3);
                }
            }
        }
    }

    /**
     * Fills the selected region randomly with walls of the state
     */
    public static void randomWalls(World world, BlockPos start, BlockPos end, BlockState state, boolean replace, double chance) {
        int height = Math.max(start.getY(), end.getY());
        int bottom = Math.min(start.getY(), end.getY());

        for (int x : IntStream.range(Math.min(start.getX(), end.getX() + 1), Math.max(start.getX(), end.getX() + 1)).toArray()) {
            if (random.nextDouble() < chance) {
                fill(world, new BlockPos(x, bottom, start.getZ()), new BlockPos(x, height, end.getZ()), state, replace);
            }
        }

        for (int z : IntStream.range(Math.min(start.getZ(), end.getZ() + 1), Math.max(start.getZ(), end.getZ() + 1)).toArray()) {
            if (random.nextDouble() < chance) {
                fill(world, new BlockPos(start.getX(), bottom, z), new BlockPos(end.getX(), height, z), state, replace);
            }
        }
    }

    /**
     * Fills the selected region randomly with cubes of size
     * @param chance should be lower than 0.01 or something
     */
    public static void randomCubes(World world, BlockPos start, BlockPos end, BlockState state, int size, boolean replace, double chance) {
        for (BlockPos pos : BlockPos.betweenClosed(start, end.offset(scale(unitTo(end, start), size)))) {
            if (random.nextDouble() < chance) {
                BlockPos cube = pos.offset(scale(unitTo(start, end), size));
                fill(world, pos, cube, state, replace);
            }
        }
    }
}
