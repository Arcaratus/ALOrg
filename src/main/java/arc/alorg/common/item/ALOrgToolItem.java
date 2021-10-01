package arc.alorg.common.item;

import arc.alorg.common.entity.XorgEntity;
import arc.alorg.common.util.BlockUtil;
import arc.alorg.common.util.ItemNBTHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ALOrgToolItem extends Item {
    private static final String TAG_ID = "id";

    private int id;

    public ALOrgToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (entity.level.isClientSide) return ActionResultType.PASS;

        if (entity instanceof XorgEntity) {
            XorgEntity xorg = (XorgEntity) entity;

            if (player.isShiftKeyDown()) {
                int id = getID(stack);
                xorg.setID(id);
                player.sendMessage(new TranslationTextComponent("message.alorg.set_id", id), Util.NIL_UUID);
            } else {
                player.sendMessage(new TranslationTextComponent("message.alorg.id", xorg.getID()), Util.NIL_UUID);
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @NotNull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockUtil.hollowCube(world, pos, pos.offset(3, 3, 3), Blocks.GLASS.defaultBlockState());
        return ActionResultType.sidedSuccess(world.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flags) {
        tooltip.add(new TranslationTextComponent("tooltip.alorg.id", getID(stack)));
    }

    public static int getID(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_ID, 1);
    }

    public static void setID(ItemStack stack, int id) {
        ItemNBTHelper.setInt(stack, TAG_ID, id);
    }
}
