package arc.alorg.client.render.entity;

import arc.alorg.ALOrg;
import arc.alorg.common.entity.XorgEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class XorgRenderer extends BipedRenderer<XorgEntity, BipedModel<XorgEntity>> {
    private static final ResourceLocation TEXTURE = ALOrg.rl("textures/entity/xorg.png");

    public XorgRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new XorgModel(), 0);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(XorgEntity entity) {
        return TEXTURE;
    }

    private static class XorgModel extends BipedModel<XorgEntity> {
        XorgModel() {
            super(0, 0, 64, 64);
        }
    }
}
