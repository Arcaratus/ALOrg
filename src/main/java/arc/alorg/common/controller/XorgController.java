package arc.alorg.common.controller;

import arc.alorg.common.entity.XorgEntity;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class XorgController extends Controller {
    private static final double LEARNING_RATE = 0.05;
    private static final int LAYER_SIZE = 32;
    private static final int NUM_INPUTS = 8;

    private XorgEntity xorg;

    public XorgController(XorgEntity xorg) {
        this.xorg = xorg;

        int numLabelClasses = 9;

        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .graphBuilder()
                .addInputs("inputs")
                .setOutputs("M1")
                .addLayer("L1", new GravesLSTM.Builder()
                        .nIn(NUM_INPUTS)
                        .nOut(LAYER_SIZE)
                        .activation(Activation.SOFTSIGN)
                        .weightInit(WeightInit.DISTRIBUTION)
                        .build(), "inputs")
                .addLayer("H1", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.DISTRIBUTION)
                        .nIn(LAYER_SIZE).nOut(LAYER_SIZE).build(), "L1")
                .addLayer("M1", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.DISTRIBUTION)
                        .nIn(LAYER_SIZE).nOut(numLabelClasses).build(), "H1")
                .backpropType(BackpropType.Standard)
                .build();

        model = new ComputationGraph(conf);
        model.init();
    }
}
