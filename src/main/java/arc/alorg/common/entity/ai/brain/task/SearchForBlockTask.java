package arc.alorg.common.entity.ai.brain.task;

import arc.alorg.common.block.GoalBlock;
import arc.alorg.common.entity.XorgEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class SearchForBlockTask extends Task<XorgEntity> {
    private final int closeEnoughDistance;

    public SearchForBlockTask(int distance) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.closeEnoughDistance = distance;
    }

    @Override
    protected void start(ServerWorld world, XorgEntity entity, long p_212831_3_) {
        AxisAlignedBB bb = new AxisAlignedBB(entity.blockPosition()).inflate(6);
        BlockState goalState = world.getBlockStates(bb).filter(b -> b.getBlock() instanceof GoalBlock).findFirst().orElse(null);
        if (goalState != null) {
//            entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, GlobalPos.of(world.dimension(), goalState.));
        }
    }
}
