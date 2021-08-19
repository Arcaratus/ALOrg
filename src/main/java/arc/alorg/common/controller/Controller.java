package arc.alorg.common.controller;

import arc.alorg.ALOrg;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class Controller {
    public static final String MODEL_SAVES = Paths.get("").toAbsolutePath() + "/models/";
    public static final String SAVE_FILE_PREFIX = "model_";
    public static int CURRENT_SAVE = 1;

    static {
        CURRENT_SAVE = getLatestModelNumber();
    }

    protected ComputationGraph model;

    public ComputationGraph getModel() {
        return model;
    }

    public void trainModel(DataSetIterator dataset) {
        model.fit(dataset);
    }

    public INDArray[] getOutput(DataSet dataset) {
        return model.output(dataset.getFeatures());
    }

    public static int getLatestModelNumber() {
        String[] files = new File(MODEL_SAVES).list();
        if (files != null) {
            return Integer.parseInt(files[files.length - 1].substring(SAVE_FILE_PREFIX.length()));
        }

        return 1;
    }

    public void saveModel() {
        try {
            File saveFile = new File(MODEL_SAVES + ++CURRENT_SAVE);
            model.save(saveFile);
            ALOrg.LOGGER.info("Successfully saved model #" + CURRENT_SAVE);
        } catch (IOException e) {}
    }
}
