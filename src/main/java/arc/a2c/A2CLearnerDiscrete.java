package arc.a2c;

import org.deeplearning4j.rl4j.learning.async.IAsyncGlobal;
import org.deeplearning4j.rl4j.learning.async.UpdateAlgorithm;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.AdvantageActorCriticUpdateAlgorithm;
import org.deeplearning4j.rl4j.learning.configuration.A3CLearningConfiguration;
import org.deeplearning4j.rl4j.learning.listener.TrainingListenerList;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.ac.IActorCritic;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.factory.Nd4j;

public class A2CLearnerDiscrete<OBSERVATION extends Encodable> extends LearnerDiscrete<OBSERVATION, IActorCritic> {
    final protected A3CLearningConfiguration configuration;
    final protected IAsyncGlobal<IActorCritic> asyncGlobal;

    final private Random rnd;

    public A2CLearnerDiscrete(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, IAsyncGlobal<IActorCritic> asyncGlobal, A3CLearningConfiguration a3cc, TrainingListenerList listeners) {
        super(asyncGlobal, mdp, listeners);
        this.configuration = a3cc;
        this.asyncGlobal = asyncGlobal;

        Long seed = configuration.getSeed();
        rnd = Nd4j.getRandom();
        if (seed != null) {
            rnd.setSeed(seed);
        }

        setUpdateAlgorithm(buildUpdateAlgorithm());
    }

    @Override
    public A3CLearningConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public IAsyncGlobal<IActorCritic> getAsyncGlobal() {
        return asyncGlobal;
    }

    @Override
    protected Policy<Integer> getPolicy(IActorCritic net) {
        return new ACPolicy<OBSERVATION>(net, true, rnd);
    }

    /**
     * calc the gradients based on the n-step rewards
     */
    @Override
    protected UpdateAlgorithm<IActorCritic> buildUpdateAlgorithm() {
        int[] shape = getHistoryProcessor() == null ? getMdp().getObservationSpace().getShape() : getHistoryProcessor().getConf().getShape();
        return new AdvantageActorCriticUpdateAlgorithm(asyncGlobal.getTarget().isRecurrent(), shape, getMdp().getActionSpace().getSize(), configuration.getGamma());
    }
}
