package arc.alorg.common.controller;

import arc.alorg.ALOrg;
import arc.alorg.common.entity.XorgEntity;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.Random;

public class XorgMDP implements MDP<XorgState, Integer, DiscreteSpace> {
    public static int count = 1;

    private static final double CANNOT_ACT_REWARD = -2;
    private static final double ACTED_BUT_STUCK_REWARD = -0.1;
    private static final double CAN_MOVE_REWARD = 2;

    private static final double REWARD_DISTANCE_SCALE = 10;

    private static final int NUM_FEATURES = 17;

    private final Random rand;

    private DiscreteSpace actionSpace = new DiscreteSpace(XorgEntity.NUM_ACTIONS);
    private final ObservationSpace<XorgState> observationSpace = new ArrayObservationSpace<>(new int[]{NUM_FEATURES});

    private boolean done;
//    private boolean goalReached;

    //    private World world;
    private XorgEntity xorg;
//    private BlockPos goalPos;

    public XorgMDP(XorgEntity xorg, Random rand) {
        this.rand = rand;

//        this.world = world;
        this.xorg = xorg;
//        this.goalPos = goalPos;
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public ObservationSpace<XorgState> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public XorgState reset() {
        done = false;
//        goalReached = false;

        return new XorgState(0, 0, 0, 0, 0, 0, 0, new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    }

    @Override
    public StepReply<XorgState> step(Integer action) {
        double reward = ACTED_BUT_STUCK_REWARD;
        if (!done) {
//            ALOrg.LOGGER.info("Action: " + action);
            xorg.act(action);

            if (!xorg.acted) {
                reward = CANNOT_ACT_REWARD;
            } else if (!xorg.isStuckMoreThan(60)) {
                reward = CAN_MOVE_REWARD;
            }
        }

//        ALOrg.LOGGER.info("RESULT = " + xorg.acted);

        xorg.acted = false;
        XorgState state = xorg.getXorgState();
        return new StepReply<>(state, reward * REWARD_DISTANCE_SCALE / Math.pow(state.distance, 2), done, null);
    }

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public MDP<XorgState, Integer, DiscreteSpace> newInstance() {
        return new XorgMDP(xorg, rand);
    }
}
