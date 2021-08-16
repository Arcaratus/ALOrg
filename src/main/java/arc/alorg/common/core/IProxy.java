package arc.alorg.common.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface IProxy {

    default void registerHandlers() {}

    default boolean isTheClientPlayer(LivingEntity entity) {
        return false;
    }

    default PlayerEntity getClientPlayer() {
        return null;
    }

}
