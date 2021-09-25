package arc.alorg.common.controller;

import arc.a2c.A2CDiscrete;
import arc.a2c.A2CDiscreteDense;
import arc.alorg.ALOrg;
import arc.alorg.common.entity.XorgEntity;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.stats.StatsListener;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.nd4j.linalg.learning.config.Adam;

import java.io.IOException;
import java.util.Random;

public class XorgController extends Controller {
    public static UIServer uiServer = UIServer.getInstance();
    public static StatsStorage statsStorage = new InMemoryStatsStorage();

    static {
        uiServer.attach(statsStorage);
    }

    private static final double LEARNING_RATE = 0.05;
    private static final int LAYER_SIZE = 32;
    private static final int NUM_INPUTS = 8;

    private XorgEntity xorg;

    private boolean isRunning;
    private XorgMDP mdp;
    private A2CDiscrete<XorgState> a2c;

    public XorgController(XorgEntity xorg) {
        this.xorg = xorg;

        int numLabelClasses = 9;
        random = new Random();
        isRunning = false;

//        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
//                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//                .graphBuilder()
//                .addInputs("inputs")
//                .setOutputs("M1")
//                .addLayer("L1", new GravesLSTM.Builder()
//                        .nIn(NUM_INPUTS)
//                        .nOut(LAYER_SIZE)
//                        .activation(Activation.SOFTSIGN)
//                        .weightInit(WeightInit.XAVIER)
//                        .build(), "inputs")
//                .addLayer("H1", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
//                        .activation(Activation.SOFTMAX)
//                        .weightInit(WeightInit.XAVIER)
//                        .nIn(LAYER_SIZE).nOut(LAYER_SIZE).build(), "L1")
//                .addLayer("M1", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
//                        .activation(Activation.SOFTMAX)
//                        .weightInit(WeightInit.XAVIER)
//                        .nIn(LAYER_SIZE).nOut(numLabelClasses).build(), "H1")
//                .backpropType(BackpropType.Standard)
//                .build();
//
//        model = new ComputationGraph(conf);
//        model.init();


        mdp = new XorgMDP(xorg, random);
        A3CDiscrete.A3CConfiguration A3C_MODEL = new A3CDiscrete.A3CConfiguration(
                random.nextInt(),    //Random seed
                10,    //Max step By epoch
                1,      //Max step
                1,         //Number of threads
                1,             //t_max
                0,        //num step noop warmup
                0.01,     //reward scaling
                0.99,          //gamma
                1.0         //td-error clipping
        );

        ActorCriticFactorySeparateStdDense.Configuration A3C_NET =  ActorCriticFactorySeparateStdDense.Configuration
                .builder().updater(new Adam(0.1)).useLSTM(true).l2(0).numHiddenNodes(34).numLayer(5).build();

        a2c = new A2CDiscreteDense<>(mdp, A3C_NET, A3C_MODEL);
    }

    public void setDone(boolean done) {
        mdp.setDone(done);
    }

    public void runA2C() {
        if (!isRunning) {
            a2c.init();
            isRunning = true;
        }

        ALOrg.LOGGER.info("Stepping...");
        a2c.train();
        saveA3C();
    }

    public void stopA2C() {
        isRunning = false;
        a2c.terminate();
        a2c.train();
    }

    public void saveA3C() {
        try {
            ACPolicy<XorgState> policy = a2c.getPolicy();
            policy.save(MODEL_SAVES + "val1/", MODEL_SAVES + "pol1");
            ALOrg.LOGGER.info("Successfully saved policy.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
