package arc.alorg.data;

import arc.alorg.ALOrg;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemModelGenerator extends ItemModelProvider {
    private static final ResourceLocation GENERATED = new ResourceLocation("item/generated");
    private static final ResourceLocation HANDHELD = new ResourceLocation("item/handheld");

    public ItemModelGenerator(DataGenerator gen, ExistingFileHelper fileHelper) {
        super(gen, ALOrg.MOD_ID, fileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "ALOrg item models";
    }

    @Override
    protected void registerModels() {
        Set<Item> items = Registry.ITEM.stream().filter(i -> ALOrg.MOD_ID.equals(Registry.ITEM.getKey(i).getNamespace()))
                .collect(Collectors.toSet());
        registerItemBlocks(takeAll(items, i -> i instanceof BlockItem).stream().map(i -> (BlockItem) i).collect(Collectors.toSet()));
//        registerItemOverrides(items);
        registerItems(items);

        takeAll(items, i -> true).forEach(this::generatedItem);
    }

    protected static String name(Item i) {
        return Registry.ITEM.getKey(i).getPath();
    }

    protected ItemModelBuilder handheldItem(String name) {
        return withExistingParent(name, HANDHELD)
                .texture("layer0", ALOrg.rl("item/" + name));
    }

    protected ItemModelBuilder handheldItem(Item i) {
        return handheldItem(name(i));
    }

    protected ItemModelBuilder generatedItem(String name) {
        return withExistingParent(name, GENERATED)
                .texture("layer0", ALOrg.rl("item/" + name));
    }

    protected ItemModelBuilder generatedItem(Item i) {
        return generatedItem(name(i));
    }

    private void registerItems(Set<Item> items) {
        takeAll(items, i -> true).forEach(this::generatedItem);
    }

    private void registerItemBlocks(Set<BlockItem> itemBlocks) {
        itemBlocks.forEach(i -> {
            String name = Registry.ITEM.getKey(i).getPath();
            withExistingParent(name, ALOrg.rl("block/" + name));
        });
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Collection<T> takeAll(Set<? extends T> src, T... items) {
        List<T> ret = Arrays.asList(items);
        for (T item : items) {
            if (!src.contains(item)) {
                ALOrg.LOGGER.warn("Item {} not found in set", item);
            }
        }
        if (!src.removeAll(ret)) {
            ALOrg.LOGGER.warn("takeAll array didn't yield anything ({})", Arrays.toString(items));
        }
        return ret;
    }

    public static <T> Collection<T> takeAll(Set<T> src, Predicate<T> pred) {
        List<T> ret = new ArrayList<>();

        Iterator<T> iter = src.iterator();
        while (iter.hasNext()) {
            T item = iter.next();
            if (pred.test(item)) {
                iter.remove();
                ret.add(item);
            }
        }

        if (ret.isEmpty()) {
            ALOrg.LOGGER.warn("takeAll predicate yielded nothing", new Throwable());
        }
        return ret;
    }
}
