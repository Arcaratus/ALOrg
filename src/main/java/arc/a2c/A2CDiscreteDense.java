package arc.a2c;

import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.configuration.A3CLearningConfiguration;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactoryCompGraph;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparate;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.network.ac.IActorCritic;
import org.deeplearning4j.rl4j.network.configuration.ActorCriticDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.util.DataManagerTrainingListener;
import org.deeplearning4j.rl4j.util.IDataManager;

public class A2CDiscreteDense <OBSERVATION extends Encodable> extends A2CDiscrete<OBSERVATION> {
    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, IActorCritic IActorCritic, A3CDiscrete.A3CConfiguration conf,
                            IDataManager dataManager) {
        this(mdp, IActorCritic, conf);
        addListener(new DataManagerTrainingListener(dataManager));
    }

    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, IActorCritic actorCritic, A3CDiscrete.A3CConfiguration conf) {
        super(mdp, actorCritic, conf.toLearningConfiguration());
    }

    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, IActorCritic actorCritic, A3CLearningConfiguration conf) {
        super(mdp, actorCritic, conf);
    }

    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, ActorCriticFactorySeparate factory,
                            A3CDiscrete.A3CConfiguration conf, IDataManager dataManager) {
        this(mdp, factory.buildActorCritic(mdp.getObservationSpace().getShape(), mdp.getActionSpace().getSize()), conf,
                dataManager);
    }

    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, ActorCriticFactorySeparate factory,
                            A3CDiscrete.A3CConfiguration conf) {
        this(mdp, factory.buildActorCritic(mdp.getObservationSpace().getShape(), mdp.getActionSpace().getSize()), conf);
    }

    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, ActorCriticFactorySeparate factory,
                            A3CLearningConfiguration conf) {
        this(mdp, factory.buildActorCritic(mdp.getObservationSpace().getShape(), mdp.getActionSpace().getSize()), conf);
    }

    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp,
                            ActorCriticFactorySeparateStdDense.Configuration netConf, A3CDiscrete.A3CConfiguration conf,
                            IDataManager dataManager) {
        this(mdp, new ActorCriticFactorySeparateStdDense(netConf.toNetworkConfiguration()), conf, dataManager);
    }

    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp,
                            ActorCriticFactorySeparateStdDense.Configuration netConf, A3CDiscrete.A3CConfiguration conf) {
        this(mdp, new ActorCriticFactorySeparateStdDense(netConf.toNetworkConfiguration()), conf);
    }

    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp,
                            ActorCriticDenseNetworkConfiguration netConf, A3CLearningConfiguration conf) {
        this(mdp, new ActorCriticFactorySeparateStdDense(netConf), conf);
    }

    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, ActorCriticFactoryCompGraph factory,
                            A3CDiscrete.A3CConfiguration conf, IDataManager dataManager) {
        this(mdp, factory.buildActorCritic(mdp.getObservationSpace().getShape(), mdp.getActionSpace().getSize()), conf,
                dataManager);
    }

    @Deprecated
    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, ActorCriticFactoryCompGraph factory,
                            A3CDiscrete.A3CConfiguration conf) {
        this(mdp, factory.buildActorCritic(mdp.getObservationSpace().getShape(), mdp.getActionSpace().getSize()), conf);
    }

    public A2CDiscreteDense(MDP<OBSERVATION, Integer, DiscreteSpace> mdp, ActorCriticFactoryCompGraph factory,
                            A3CLearningConfiguration conf) {
        this(mdp, factory.buildActorCritic(mdp.getObservationSpace().getShape(), mdp.getActionSpace().getSize()), conf);
    }
}
