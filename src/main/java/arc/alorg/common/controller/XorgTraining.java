package arc.alorg.common.controller;

import arc.alorg.common.block.ModBlocks;
import arc.alorg.common.block.tiles.TrainingTile;
import arc.alorg.common.entity.ModEntities;
import arc.alorg.common.entity.XorgEntity;
import arc.alorg.common.util.BlockUtil;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;

/**
 * An instance wrapper for Xorg Training
 */
public class XorgTraining {
    private static final String TAG_ID = "id";
    private static final String TAG_IS_RUNNING = "isRunning";
    private static final String TAG_START_POS = "startPos";
    private static final String TAG_END_POS = "endPos";

    private static Random random = new Random();

    private final int size;
    private final TrainingTile trainingTile;
    private final World world;

    private int id;
    private boolean isRunning;
    private BlockPos startPos;
    private BlockPos endPos;
    private XorgEntity xorg;

    public XorgTraining(int size, TrainingTile trainingTile, BlockPos startPos, BlockPos endPos, int id) {
        this.size = size;
        this.trainingTile = trainingTile;
        world = trainingTile.getLevel();
        this.startPos = startPos;
        this.endPos = endPos;

        this.id = id;
        isRunning = false;
    }

    public int getSize() {
        return size;
    }

    public XorgEntity getXorg() {
        return xorg;
    }

    public TrainingTile getTrainingTile() {
        return trainingTile;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setupEnvironment() {
        BlockUtil.hollowCube(world, startPos, endPos, ModBlocks.TRAINING_GLASS.defaultBlockState());

        Pair<BlockPos, BlockPos> innerPos = BlockUtil.innerPos(startPos, endPos);
        BlockUtil.randomFill(world, innerPos.getLeft(), innerPos.getRight(), Blocks.GRAY_STAINED_GLASS.defaultBlockState(), true, 0.05);

        BlockUtil.randomWalls(world, innerPos.getLeft(), innerPos.getRight(), Blocks.PINK_STAINED_GLASS.defaultBlockState(), true, 0.1);

        BlockUtil.randomCubes(world, innerPos.getLeft(), innerPos.getRight(), Blocks.BLUE_STAINED_GLASS.defaultBlockState(), 2, true, 0.005);
    }

    public void runTraining() {
        if (!isRunning) {
            isRunning = true;

            if (xorg == null) {
                xorg = new XorgEntity(ModEntities.XORG, world);
            }

            Pair<BlockPos, BlockPos> innerPos = BlockUtil.innerPos(startPos, endPos);
            BlockPos innerStart = innerPos.getLeft();
            BlockPos innerEnd = innerPos.getRight();

            BlockPos center = new BlockPos((innerStart.getX() + innerEnd.getX()) / 2, innerStart.getY(), (innerStart.getZ() + innerEnd.getZ()) / 2);
            xorg.setPos(center.getX(), center.getY(), center.getZ());

            world.setBlock(center, Blocks.AIR.defaultBlockState(), 3);
            world.setBlock(center.above(), Blocks.AIR.defaultBlockState(), 3);

            world.addFreshEntity(xorg);

            BlockPos goalPos = new BlockPos(random.ints(innerStart.getX(), innerEnd.getX() + 1).findFirst().getAsInt(), random.ints(innerStart.getY(), innerStart.getY() + 7).findFirst().getAsInt(), random.ints(innerStart.getZ(), innerEnd.getZ() + 1).findFirst().getAsInt());
            world.setBlock(goalPos, ModBlocks.GOAL.defaultBlockState(), 3);
        }
    }

    public void writePacketNBT(CompoundNBT cmp) {
        cmp.putInt(TAG_ID, id);
        cmp.putBoolean(TAG_IS_RUNNING, isRunning);
        cmp.putLong(TAG_START_POS, startPos.asLong());
        cmp.putLong(TAG_END_POS, endPos.asLong());
    }

    public void readPacketNBT(CompoundNBT cmp) {
        id = cmp.getInt(TAG_ID);
        isRunning = cmp.getBoolean(TAG_IS_RUNNING);
        startPos = BlockPos.of(cmp.getLong(TAG_START_POS));
        endPos = BlockPos.of(cmp.getLong(TAG_END_POS));
    }
}
