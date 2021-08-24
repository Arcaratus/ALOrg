package arc.alorg.common.item;

import arc.alorg.ALOrg;
import arc.alorg.common.core.ALOrgCreativeTab;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModItems {

    public static final Item ALORG_TOOL = new ALOrgToolItem(unstackable());

    public static Item.Properties defaultBuilder() {
        return new Item.Properties().tab(ALOrgCreativeTab.INSTANCE);
    }

    private static Item.Properties unstackable() {
        return defaultBuilder().stacksTo(1);
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();
        ALOrg.register(r, "alorg_tool", ALORG_TOOL);
    }
}
