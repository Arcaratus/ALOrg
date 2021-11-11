package arc.alorg.common.controller;

import arc.a2c.A2CDiscrete;
import arc.a2c.A2CDiscreteDense;
import arc.alorg.ALOrg;
import arc.alorg.common.entity.XorgEntity;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.nd4j.linalg.learning.config.Adam;

import java.io.IOException;
import java.util.Random;

public class XorgController extends Controller {
    private static final double LEARNING_RATE = 0.05;
    private static final int LAYER_SIZE = 32;
    private static final int NUM_INPUTS = 8;

    private int id;

    private boolean isRunning;
    private XorgMDP mdp;
    private A2CDiscrete<XorgState> a2c;

    public XorgController(XorgEntity xorg) {
        id = 1;

        random = new Random();
        isRunning = false;

        mdp = new XorgMDP(xorg, random);
        A3CDiscrete.A3CConfiguration A3C_MODEL = new A3CDiscrete.A3CConfiguration(
                random.nextInt(),    //Random seed
                100000,    //Max step By epoch
                1,      //Max step
                1,         //Number of threads
                1,             //t_max
                4,        //num step noop warmup
                0.1,     //reward scaling
                0.99,          //gamma
                1.0         //td-error clipping
        );

        ActorCriticFactorySeparateStdDense.Configuration A3C_NET = ActorCriticFactorySeparateStdDense.Configuration.builder()
                .updater(new Adam(0.01))
                .useLSTM(true)
                .l2(0)
                .numHiddenNodes(36)
                .numLayer(5)
                .build();

        a2c = new A2CDiscreteDense<>(mdp, A3C_NET, A3C_MODEL);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
        a2c.setID(id);
    }

    public void runA2C() {
        if (!isRunning) {
            a2c.init();
            isRunning = true;
        }

        a2c.train();
    }

    public void stopA2C() {
        isRunning = false;
        a2c.terminate();
        a2c.train();

        saveA2C();
    }

    public void doAction(XorgState state) {
//        xorg.act(a2c.getPolicy().nextAction(state));
    }

    public void saveA2C() {
        try {
            ACPolicy<XorgState> policy = a2c.getPolicy();
            policy.save(MODEL_SAVES + "val" + id, MODEL_SAVES + "pol" + id);
            ALOrg.LOGGER.info("Successfully saved policy: " + id);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void loadA2C(int id) {
        setID(id);

        try {
            ACPolicy<XorgState> policy = ACPolicy.load(MODEL_SAVES + "val" + id, MODEL_SAVES + "pol" + id);
            a2c.setPolicy(policy);
            ALOrg.LOGGER.info("Successfully loaded policy: " + id);
        } catch (IOException e) {
            ALOrg.LOGGER.info("Policy " + id + " does not exist, creating new one...");
        }
    }
}
