package arc.a2c;

import org.deeplearning4j.rl4j.learning.async.AsyncGlobal;
import org.deeplearning4j.rl4j.learning.configuration.A3CLearningConfiguration;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.ac.IActorCritic;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.factory.Nd4j;

public abstract class A2CDiscrete<OBSERVATION extends Encodable> extends NormalLearning<OBSERVATION, Integer, DiscreteSpace, IActorCritic> {

    final public A3CLearningConfiguration configuration;

    final protected MDP<OBSERVATION, Integer, DiscreteSpace> mdp;
    final private IActorCritic iActorCritic;

    final private AsyncGlobal asyncGlobal;

    private ACPolicy<OBSERVATION> policy;

    public A2CDiscrete(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, IActorCritic iActorCritic, A3CLearningConfiguration conf) {
        this.iActorCritic = iActorCritic;
        this.mdp = mdp;
        this.configuration = conf;
        asyncGlobal = new AsyncGlobal<>(iActorCritic, conf);

        Long seed = conf.getSeed();
        Random rnd = Nd4j.getRandom();
        if (seed != null) {
            rnd.setSeed(seed);
        }

        policy = new ACPolicy<>(iActorCritic, true, rnd);
    }

    public A2CLearnerDiscrete newLearner() {
        return new A2CLearnerDiscrete(mdp.newInstance(), asyncGlobal, this.getConfiguration(), getListeners(), getID());
    }

    @Override
    public A3CLearningConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public MDP<OBSERVATION, Integer, DiscreteSpace> getMdp() {
        return mdp;
    }

    @Override
    public AsyncGlobal getAsyncGlobal() {
        return asyncGlobal;
    }

    @Override
    public ACPolicy<OBSERVATION> getPolicy() {
        return policy;
    }

    public void setPolicy(ACPolicy<OBSERVATION> policy) {
        this.policy = policy;
    }

    public IActorCritic getNeuralNet() {
        return iActorCritic;
    }
}
