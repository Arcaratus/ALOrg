package arc.alorg.common.entity;

import arc.alorg.ALOrg;
import arc.alorg.common.block.ModBlocks;
import arc.alorg.common.controller.XorgController;
import arc.alorg.common.controller.XorgMDP;
import arc.alorg.common.controller.XorgState;
import arc.alorg.common.entity.ai.brain.task.SearchForBlockTask;
import arc.alorg.common.util.BlockUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Arrays;

public class XorgEntity extends CreatureEntity {
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    private static final ImmutableList<SensorType<? extends Sensor<? super XorgEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY);

    private static ImmutableList<Pair<Integer, ? extends Task<? super XorgEntity>>> CORE = ImmutableList.of(Pair.of(0, new SwimTask(0.8F)), Pair.of(0, new LookTask(45, 90)), Pair.of(1, new WalkToTargetTask()), Pair.of(2, new SearchForBlockTask(b -> b == ModBlocks.GOAL, 1)));
    private static ImmutableList<Pair<Integer, ? extends Task<? super XorgEntity>>> IDLE = ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new SearchForBlockTask(b -> b == ModBlocks.GOAL, 1), 1)
            , Pair.of(new WalkTowardsLookTargetTask(0.5F, 2), 1)
    ))));

    private static final BlockPos[] SURROUNDING = new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(0, 0, 1), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(-1, 0, 0), new BlockPos(0, 1, 1), new BlockPos(1, 1, 0), new BlockPos(0, 1, -1), new BlockPos(-1, 1, 0), new BlockPos(0, 2, 0) };

    private static final double SPEED_THRESHOLD = 0.1;
    private static final BlockState BUILDING_BLOCK = Blocks.PINK_CONCRETE.defaultBlockState();

    public XorgController controller;

    private long lastTimeCheck;
    private BlockPos goalPos;
    private BlockPos nextPos;

    public boolean trainingRunning = false;
    public boolean acted = false;

    public XorgEntity(EntityType<? extends XorgEntity> type, World world) {
        super(type, world);
        controller = new XorgController(this);

        goalPos = BlockPos.ZERO;
        nextPos = BlockPos.ZERO;
        lastTimeCheck = world.getGameTime();
    }

    @Override
    public Brain<XorgEntity> getBrain() {
        return (Brain<XorgEntity>)super.getBrain();
    }

    @Override
    protected Brain.BrainCodec<XorgEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> idk) {
        Brain<XorgEntity> brain = brainProvider().makeBrain(idk);
        registerBrainGoals(brain);
        return brain;
    }

    public void refreshBrain(ServerWorld world) {
        Brain<XorgEntity> brain = getBrain();
        brain.stopAll(world, this);
        this.brain = brain.copyWithoutBehaviors();
        registerBrainGoals(getBrain());
    }

    private void registerBrainGoals(Brain<XorgEntity> brain) {
        brain.addActivity(Activity.CORE, CORE);
        brain.addActivity(Activity.IDLE, IDLE);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setActiveActivityIfPossible(Activity.IDLE);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected void customServerAiStep() {
        level.getProfiler().push("xorgBrain");
        getBrain().tick((ServerWorld) level, this);
        level.getProfiler().pop();

        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
//        kill();
        if (!level.isClientSide() && level.getGameTime() % 10 == 0) {
            if (getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
                goalPos = getBrain().getMemory(MemoryModuleType.WALK_TARGET).get().getTarget().currentBlockPosition();

                if (getNavigation().getPath() != null) {
                    nextPos = getNavigation().getPath().getNextNodePos();
                }

                if (getDeltaMovement().length() >= SPEED_THRESHOLD) {
                    ALOrg.LOGGER.info("Moving...");
                    lastTimeCheck = level.getGameTime();
                } else if (reachedGoal()) {
                    if (trainingRunning) {
                        ALOrg.LOGGER.info("Reached goal!");
                        trainingRunning = false;
                        controller.setDone(true);
                        controller.stopA2C();
                    }
                } else if (isStuckMoreThan(10)) {
                    trainingRunning = true;
                    controller.runA2C();
                }
            } else {
                ALOrg.LOGGER.info("No memory");
                lastTimeCheck = level.getGameTime();
                goalPos = BlockPos.ZERO;
                nextPos = BlockPos.ZERO;
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);

        if (level instanceof ServerWorld) {
            refreshBrain((ServerWorld) level);
        }
    }

    public XorgState getXorgState() {
        Vector3d goalDiff = position().vectorTo(Vector3d.atCenterOf(goalPos));
        Vector3d nextDiff = position().vectorTo(Vector3d.atCenterOf(nextPos));
        return new XorgState(goalDiff.x, goalDiff.y, goalDiff.z, goalDiff.length(), nextDiff.x, nextDiff.y, nextDiff.z, getSurroundingBlocks());
    }

    public boolean isStuckMoreThan(long time) {
        ALOrg.LOGGER.info("DIFF: " + (level.getGameTime() - lastTimeCheck));
        return getDeltaMovement().length() < SPEED_THRESHOLD && level.getGameTime() - lastTimeCheck >= time;
    }

    public boolean reachedGoal() {
        return goalPos != BlockPos.ZERO && position().distanceTo(Vector3d.atCenterOf(goalPos)) <= 1.5;
    }

    // Only for horizontal directions, starts from block underneath first
    public void tryBuild(int direction) {
        Direction dir = Direction.values()[direction + 2];
        BlockPos pos = blockPosition().offset(dir.getNormal()).below();
        if (BlockUtil.isAir(level, pos)) {
            level.setBlock(pos, BUILDING_BLOCK, 3);
            acted = true;
            return;
        }

        pos = pos.above();
        if (BlockUtil.isAir(level, pos)) {
            level.setBlock(pos, BUILDING_BLOCK, 3);
            acted = true;
        }
    }

    // Scuffed probably needs fixing
    public void tryJumpAndPlaceBlockUnderneath() {
        BlockPos pos = blockPosition().above(2);
        if (BlockUtil.isAir(level, pos)) {
            jumpFromGround();
            level.setBlock(blockPosition(), BUILDING_BLOCK, 3);
            acted = true;
        }
    }

    // All directions -> horizontal breakage starts from top then bottom
    public void tryBreak(int direction) {
        Direction dir = Direction.values()[direction - XorgMDP.ACTION_BREAK_DOWN];
        BlockPos pos = blockPosition().offset(dir.getNormal());

        if (direction > 5) {
            pos = pos.above();
        }

        // First air check for horizontal breakage
        if (BlockUtil.isAir(level, pos)) {
            if (direction > XorgMDP.ACTION_BREAK_UP) {
                pos = pos.below();
            } else {
                return;
            }
        }

        // No block to break :(
        if (BlockUtil.isAir(level, pos)) {
            return;
        }

        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock().getExplosionResistance(blockState, level, pos, null) < 20) { // idk if this is correct
            level.destroyBlock(pos, false, this);
            acted = true;
        }
    }

    // Returns a double[10] where s[0] is the bottom block, s[1-8] are the sides, and s[9] is the block above
    public double[] getSurroundingBlocks() {
        double[] surrounding = new double[10];

        for (int i = 0; i < SURROUNDING.length; i++) {
            surrounding[i] = blockToInt(level.getBlockState(blockPosition().offset(SURROUNDING[i])));
        }

        return surrounding;
    }

    // 1 - air block, -1 - nonsolid block, -1 - normal block, -2 - hard block, 0 - other
    private int blockToInt(BlockState blockState) {
        if (blockState.getBlock() == Blocks.AIR) {
            return 1;
        } else if (!blockState.getMaterial().isSolid()) {
            return -1;
        } else if (blockState.getBlock().getExplosionResistance() > 20) {
            return -2;
        } else if (!blockState.getMaterial().isLiquid()) {
            return 0;
        }

        return -4;
    }
}
