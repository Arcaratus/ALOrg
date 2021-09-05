import arc.a2c.A2CDiscreteDense;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.space.Box;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.nd4j.graph.UIEventSubtype.LEARNING_RATE;

public class NNTest {
    public static final String MODEL_SAVES = Paths.get("").toAbsolutePath() + "/models/";

    public static void main(String[] args) {
//        initNN();
        initA3C();
    }

    public static void initNN() {
        final int LAYER_SIZE = 9;
        final int NUM_INPUTS = 8;
        int numLabelClasses = 9;

        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam(LEARNING_RATE))
                .graphBuilder()
                .addInputs("inputs")
                .setOutputs("M1")
                .addLayer("L1", new LSTM.Builder()
                        .nIn(NUM_INPUTS)
                        .nOut(LAYER_SIZE)
                        .activation(Activation.SOFTSIGN)
                        .weightInit(WeightInit.XAVIER)
                        .build(), "inputs")
//                .addLayer("H1", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
//                        .activation(Activation.SOFTMAX)
//                        .weightInit(WeightInit.XAVIER)
//                        .nIn(LAYER_SIZE).nOut(LAYER_SIZE).build(), "L1")
                .addLayer("M1", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .nIn(LAYER_SIZE).nOut(numLabelClasses).build(), "L1")
                .backpropType(BackpropType.Standard)
                .build();

        ComputationGraph model = new ComputationGraph(conf);
        model.init();
        double[] inputsArray = new double[] {0, 1, 2, 3, 4, 5, 6, 7};
        double[] outputsArray = new double[] {0, 1, 4, 9, 16, 27, 36, 49, 27};
        List<Pair<INDArray, INDArray>> dataset = new ArrayList<>();

        INDArray inputs = Nd4j.create(inputsArray, new int[]{1, 8, 1});
        INDArray desiredOutputs = Nd4j.create(outputsArray, new int[]{1, outputsArray.length, 1});
        dataset.add(Pair.of(inputs, desiredOutputs));
        dataset.add(Pair.of(inputs, desiredOutputs));

        DataSetIterator dataSetIterator = new INDArrayDataSetIterator(dataset,2);

        INDArray idk = Nd4j.create(2, 8, 1);
        INDArray idk2 = Nd4j.create(2, 9, 1);
        for (int i = 0; i < 10; i++) {
//            model.fit(new INDArray[]{idk}, new INDArray[]{idk2});
            model.fit(dataSetIterator);
        }

        double[] test = new double[] {1, 2, 3, 4, 5, 6, 7, 8};
        INDArray testInputs = Nd4j.create(test, new int[]{1, 8, 1});
        INDArray[] output = model.output(testInputs);
        System.out.println(output[0]);
    }

    public static void initA3C() {
        TestMDP mdp = new TestMDP();
//        IHistoryProcessor.Configuration HP = IHistoryProcessor.Configuration.builder()
//                .historyLength(4)
//                .rescaledWidth(84)
//                .rescaledHeight(110)
//                .croppingHeight(84)
//                .croppingWidth(84)
//                .offsetX(0)
//                .offsetY(0)
//                .skipFrame(4)
//                .build();

        A3CDiscrete.A3CConfiguration A3C_MODEL = A3CDiscrete.A3CConfiguration.builder()
                .seed(123)
                .maxEpochStep(1)
                .maxStep(10)
                .nstep(1)
                .numThread(1)
                .updateStart(1)
                .rewardFactor(0.1)
                .gamma(0.99)
                .errorClamp(1)
                .build();

        ActorCriticFactorySeparateStdDense.Configuration A3C_NET = ActorCriticFactorySeparateStdDense.Configuration
                .builder().updater(new Adam(0.001)).l2(0).numHiddenNodes(8).numLayer(1).build();

//        A3CDiscreteDense<Box> a3c = new A3CDiscreteDense<>(mdp, A3C_NET, A3C_MODEL);
        A2CDiscreteDense<Box> a3c = new A2CDiscreteDense<>(mdp, A3C_NET, A3C_MODEL);
        System.out.println("Starting training...");
        a3c.train();

        System.out.println("Finished training.");
        ACPolicy<Box> pol = a3c.getPolicy();

        try {
            pol.save(MODEL_SAVES + "/val1", MODEL_SAVES + "/pol1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
