package arc.alorg.common.entity;

import arc.alorg.common.controller.XorgController;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class XorgEntity extends CreatureEntity {
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    private static final ImmutableList<SensorType<? extends Sensor<? super XorgEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY);

    private static ImmutableList<Pair<Integer, ? extends Task<? super XorgEntity>>> CORE = ImmutableList.of(Pair.of(0, new SwimTask(0.8F)), Pair.of(0, new LookTask(45, 90)), Pair.of(1, new WalkToTargetTask()));
    private static ImmutableList<Pair<Integer, ? extends Task<? super XorgEntity>>> IDLE = ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new FindWalkTargetTask(0.5F), 1), Pair.of(new WalkTowardsLookTargetTask(0.5F, 2), 1), Pair.of(new DummyTask(30, 60), 1)))), Pair.of(3, new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)));

    public XorgController controller;

    public XorgEntity(EntityType<? extends XorgEntity> type, World world) {
        super(type, world);
        controller = new XorgController(this);
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
//        level.getProfiler().push("xorgBrain");
//        getBrain().tick((ServerWorld) level, this);
//        level.getProfiler().pop();



        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
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
}
