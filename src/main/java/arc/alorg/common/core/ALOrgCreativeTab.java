package arc.alorg.common.core;

import arc.alorg.ALOrg;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.Nonnull;

public class ALOrgCreativeTab extends ItemGroup {
    public static final ALOrgCreativeTab INSTANCE = new ALOrgCreativeTab();

    public ALOrgCreativeTab() {
        super(ALOrg.MOD_ID);
    }

    @Nonnull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(Items.SUGAR);
    }
}
