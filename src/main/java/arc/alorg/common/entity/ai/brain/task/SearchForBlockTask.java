package arc.alorg.common.entity.ai.brain.task;

import arc.alorg.common.entity.XorgEntity;
import arc.alorg.common.util.MathUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Predicate;

public class SearchForBlockTask extends Task<XorgEntity> {
    private final Predicate<Block> blockPredicate;
    private final int closeEnoughDistance;

    public SearchForBlockTask(Predicate<Block> blockPredicate, int distance) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.blockPredicate = blockPredicate;
        this.closeEnoughDistance = distance;
    }

    @Override
    protected void start(ServerWorld world, XorgEntity entity, long p_212831_3_) {
        int yRange = 14;
        int xzRange = 28;
        for (int y = 0; y < yRange + 1; y++) {
            int dy = MathUtil.coneFunc(y);
            for (int x = 0; x < xzRange + 1; x++) {
                int dx = MathUtil.coneFunc(x);
                for (int z = 0; z < xzRange + 1; z++) {
                    int dz = MathUtil.coneFunc(z);
                    BlockPos searchPos = entity.blockPosition().offset(dx, dy, dz);
                    BlockState searchState = world.getBlockState(searchPos);
                    if (blockPredicate.test(searchState.getBlock())) {
                        entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(searchPos, 0.5F, closeEnoughDistance));
                        return;
                    }
                }
            }
        }

//        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}
