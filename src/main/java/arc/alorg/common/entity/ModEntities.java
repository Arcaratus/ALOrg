package arc.alorg.common.entity;

import arc.alorg.ALOrg;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModEntities {
    public static final EntityType<XorgEntity> XORG = EntityType.Builder.<XorgEntity>of(XorgEntity::new, EntityClassification.CREATURE)
            .sized(0.6F, 1.8F)
            .setTrackingRange(16)
            .setUpdateInterval(10)
            .setShouldReceiveVelocityUpdates(true)
            .build("");

    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        IForgeRegistry<EntityType<?>> r = event.getRegistry();
        ALOrg.register(r, "xorg", XORG);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(XORG, MobEntity.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .build());
    }
}
