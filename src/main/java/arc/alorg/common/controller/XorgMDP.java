package arc.alorg.common.controller;

import arc.alorg.common.entity.XorgEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.environment.IntegerActionSchema;
import org.deeplearning4j.rl4j.environment.Schema;
import org.deeplearning4j.rl4j.environment.StepResult;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.rng.Random;

public class XorgMDP implements MDP<XorgState, Integer, DiscreteSpace> {
    private static final double BAD_MOVE_REWARD = -0.1;
    private static final double GOAL_REWARD = 4.0;
    private static final double TRAP_REWARD = -4.0;
    private static final double BRANCH_REWARD = 1.0;

    private static final int NUM_FEATURES = 4;

    public static final int NUM_ACTIONS = 11;

    // Don't change order of anything
    public static final int ACTION_BUILD_Z_NEG = 0;
    public static final int ACTION_BUILD_Z = 1;
    public static final int ACTION_BUILD_X_NEG = 2;
    public static final int ACTION_BUILD_X = 3;

    public static final int ACTION_BUILD_DOWN = 4;

    public static final int ACTION_BREAK_DOWN = 5;
    public static final int ACTION_BREAK_UP = 6;

    public static final int ACTION_BREAK_Z_NEG = 7;
    public static final int ACTION_BREAK_Z = 8;
    public static final int ACTION_BREAK_X_NEG = 9;
    public static final int ACTION_BREAK_X = 10;


    private final Random rand;

    private DiscreteSpace actionSpace = new DiscreteSpace(NUM_ACTIONS);
    private final ObservationSpace<XorgState> observationSpace = new ArrayObservationSpace<>(new int[] {NUM_FEATURES});

    private boolean done;
    private boolean goalReached;

    private World world;
    private XorgEntity xorg;
    private BlockPos goalPos;

    private double dx, dy, dz;
    private double distance;

    public XorgMDP(World world, XorgEntity xorg, BlockPos goalPos, Random rand) {
        this.rand = rand;

        this.world = world;
        this.xorg = xorg;
        this.goalPos = goalPos;
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

    @Override
    public XorgState reset() {
        done = false;
        goalReached = false;

        dx = dy = dz = distance = 0;

        return new XorgState(dx, dy, dz, distance);
    }

    @Override
    public StepReply<XorgState> step(Integer action) {
        double reward = 0;
        if (!done) {
            switch (action) {

            }
        }

        return new StepReply<>(new XorgState(dx, dy, dz, distance), reward, done, null);
    }

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public MDP<XorgState, Integer, DiscreteSpace> newInstance() {
        return null;
    }
}
