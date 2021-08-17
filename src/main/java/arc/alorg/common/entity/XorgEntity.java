package arc.alorg.common.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class XorgEntity extends CreatureEntity {
    public XorgEntity(EntityType<XorgEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {

    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }
}
