package arc.alorg.client.core;

import arc.alorg.client.render.entity.XorgRenderer;
import arc.alorg.common.block.ModBlocks;
import arc.alorg.common.core.IProxy;
import arc.alorg.common.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {

    @Override
    public void registerHandlers() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        registerRenderTypes();
        registerEntityRenderers();
    }

    @Override
    public boolean isTheClientPlayer(LivingEntity entity) {
        return entity == Minecraft.getInstance().player;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    private static void registerRenderTypes() {
        RenderTypeLookup.setRenderLayer(ModBlocks.TRAINING_GLASS, RenderType.translucent());
    }

    private static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.XORG, XorgRenderer::new);
    }
}
