package arc.alorg.common.block.tiles;

import arc.alorg.ALOrg;
import arc.alorg.common.block.ModBlocks;
import arc.alorg.common.entity.ModEntities;
import arc.alorg.common.entity.XorgEntity;
import arc.alorg.common.util.BlockUtil;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

public class TrainingTile extends ALOrgTile implements ITickableTileEntity {
    private static final int ENVIRONMENT_SIZE = 19;

    /**
     * Note-to-self: NEVER use "id" as a tag since it is already used and will cause a MAJOR headache to debug
     */
    private static final String TAG_ID = "idNumber";
    private static final String TAG_IS_RUNNING = "isRunning";
    private static final String TAG_START_POS = "startPos";
    private static final String TAG_END_POS = "endPos";

    private static final Random random = new Random();

    private int id = 0;
    private boolean isRunning = false;
    private BlockPos startPos = BlockPos.ZERO;
    private BlockPos endPos = BlockPos.ZERO;
    private BlockPos goalPos = BlockPos.ZERO;
    private XorgEntity xorg = null;

    public TrainingTile() {
        super(ModTiles.TRAINING);
    }

    @Override
    public void tick() {
        if (level.isClientSide()) {
            return;
        }

        if (level.getGameTime() % 20 == 0) {
            if (!hasTraining()) {
                return;
            }

            if (xorg == null) {
                loadTraining();
                return;
            }

            if (trainingFinished()) {
                startTraining();
            }

            if (outOfBoundsCheck()) {
                ALOrg.LOGGER.info("Went out of bounds!");
                startTraining();
            }
        }
    }

    @Override
    public void writePacketNBT(CompoundNBT cmp) {
        cmp.putInt(TAG_ID, id);
        cmp.putBoolean(TAG_IS_RUNNING, isRunning);
        cmp.putLong(TAG_START_POS, startPos.asLong());
        cmp.putLong(TAG_END_POS, endPos.asLong());
    }

    @Override
    public void readPacketNBT(CompoundNBT cmp) {
        id = cmp.getInt(TAG_ID);
        isRunning = cmp.getBoolean(TAG_IS_RUNNING);
        startPos = BlockPos.of(cmp.getLong(TAG_START_POS));
        endPos = BlockPos.of(cmp.getLong(TAG_END_POS));
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setupTraining(int id) {
        if (level.isClientSide()) {
            return;
        }

        if (!hasTraining()) {
            BlockPos startPos = getBlockPos().south();
            BlockPos endPos = startPos.offset(ENVIRONMENT_SIZE, ENVIRONMENT_SIZE, ENVIRONMENT_SIZE);

            setID(id);
            isRunning = false;
            this.startPos = startPos;
            this.endPos = endPos;
        }

        startTraining();
    }

    public boolean hasTraining() {
        return id > 0;
    }

    public void startTraining() {
        setupEnvironment();

        runTraining();

        setChanged();
    }

    public void toggleTraining() {
        if (!hasTraining()) {
            return;
        }

        isRunning = !isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        xorg.setTraining(running);
    }

    public void setupEnvironment() {
        BlockUtil.hollowCube(level, startPos, endPos, ModBlocks.TRAINING_GLASS.defaultBlockState());

        Pair<BlockPos, BlockPos> innerPos = BlockUtil.innerPos(startPos, endPos);
        BlockUtil.randomFill(level, innerPos.getLeft(), innerPos.getRight(), Blocks.GRAY_STAINED_GLASS.defaultBlockState(), true, 0.05);

        BlockUtil.randomWalls(level, innerPos.getLeft(), innerPos.getRight(), Blocks.PINK_STAINED_GLASS.defaultBlockState(), true, 0.1);

        BlockUtil.randomCubes(level, innerPos.getLeft(), innerPos.getRight(), Blocks.BLUE_STAINED_GLASS.defaultBlockState(), 2, true, 0.005);
    }

    public void runTraining() {
        boolean spawn = false;
        if (xorg == null || !xorg.isAlive()) {
            spawn = true;
            xorg = new XorgEntity(ModEntities.XORG, level);
        }

        setRunning(true);

        Pair<BlockPos, BlockPos> innerPos = BlockUtil.innerPos(startPos, endPos);
        BlockPos innerStart = innerPos.getLeft();
        BlockPos innerEnd = innerPos.getRight();

        BlockPos center = new BlockPos((innerStart.getX() + innerEnd.getX()) / 2, innerStart.getY(), (innerStart.getZ() + innerEnd.getZ()) / 2);

        xorg.setID(id);
        xorg.setPos(center.getX(), center.getY(), center.getZ());

        level.setBlock(center, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(center.above(), Blocks.AIR.defaultBlockState(), 3);

        if (spawn)
            level.addFreshEntity(xorg);

        goalPos = new BlockPos(random.ints(innerStart.getX(), innerEnd.getX() + 1).findFirst().getAsInt(), random.ints(innerStart.getY(), innerStart.getY() + 5).findFirst().getAsInt(), random.ints(innerStart.getZ(), innerEnd.getZ() + 1).findFirst().getAsInt());
        level.setBlock(goalPos, ModBlocks.GOAL.defaultBlockState(), 3);
    }

    public boolean trainingFinished() {
        if (isRunning) {
            if (xorg.reachedGoal()) {
                setRunning(false);
            }
        }

        return !isRunning;
    }

    public boolean outOfBoundsCheck() {
        if (isRunning && xorg.isAlive()) {
            return xorg.position().y() - goalPos.getY() > 7;
        }

        return false;
    }

    public void loadTraining() {
        if (level.isClientSide()) {
            return;
        }

        List<XorgEntity> xorgs = level.getLoadedEntitiesOfClass(XorgEntity.class, new AxisAlignedBB(startPos.getX(), startPos.getY(), startPos.getZ(), endPos.getX(), endPos.getY(), endPos.getZ()));
        if (!xorgs.isEmpty()) {
            xorg = xorgs.get(0);
            xorg.setID(id);
            xorg.setTraining(true);
        }
    }
}
