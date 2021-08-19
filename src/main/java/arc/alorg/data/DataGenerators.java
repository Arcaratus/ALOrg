package arc.alorg.data;

import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class DataGenerators {
    public static void gatherData(GatherDataEvent event) {
        ExistingFileHelper helper = event.getExistingFileHelper();
        if (event.includeServer()) {

        }

        if (event.includeClient()) {
            event.getGenerator().addProvider(new BlockStateGenerator(event.getGenerator(), helper));
            event.getGenerator().addProvider(new ItemModelGenerator(event.getGenerator(), helper));
        }
    }
}
