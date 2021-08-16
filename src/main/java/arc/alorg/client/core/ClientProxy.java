package arc.alorg.client.core;

import arc.alorg.common.core.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {

    @Override
    public void registerHandlers() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
    }

    @Override
    public boolean isTheClientPlayer(LivingEntity entity) {
        return entity == Minecraft.getInstance().player;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
