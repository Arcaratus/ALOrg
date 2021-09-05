package arc.alorg.common.item;

import arc.alorg.common.entity.XorgEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class ALOrgToolItem extends Item {
    public ALOrgToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
        if (entity.level.isClientSide) return ActionResultType.PASS;

        if (entity instanceof XorgEntity) {
            XorgEntity xorg = (XorgEntity) entity;
//            System.out.println("STUCK: " + xorg.isStuck());
//            System.out.println("Vel: " + xorg.getSpeed());

            xorg.controller.runA3C();
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
