package arc.a2c;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.experience.ExperienceHandler;
import org.deeplearning4j.rl4j.experience.StateActionExperienceHandler;
import org.deeplearning4j.rl4j.learning.IHistoryProcessor;
import org.deeplearning4j.rl4j.learning.async.IAsyncGlobal;
import org.deeplearning4j.rl4j.learning.async.UpdateAlgorithm;
import org.deeplearning4j.rl4j.learning.configuration.IAsyncLearningConfiguration;
import org.deeplearning4j.rl4j.learning.listener.TrainingListenerList;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.NeuralNet;
import org.deeplearning4j.rl4j.observation.Observation;
import org.deeplearning4j.rl4j.policy.IPolicy;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;

public abstract class LearnerDiscrete<OBSERVATION extends Encodable, NN extends NeuralNet> extends Learner<OBSERVATION, Integer, DiscreteSpace, NN> {
    private NN current;
    private UpdateAlgorithm<NN> updateAlgorithm;
    private ExperienceHandler experienceHandler;

    public LearnerDiscrete(IAsyncGlobal<NN> asyncGlobal, MDP<OBSERVATION, Integer, DiscreteSpace> mdp, TrainingListenerList listeners) {
        super(mdp, listeners);
        synchronized (asyncGlobal) {
            current = (NN) asyncGlobal.getTarget().clone();
        }

        StateActionExperienceHandler.Configuration experienceHandlerConfiguration = StateActionExperienceHandler.Configuration.builder()
                .batchSize(getNStep())
                .build();
        experienceHandler = new StateActionExperienceHandler(experienceHandlerConfiguration);
    }

    private int getNStep() {
        IAsyncLearningConfiguration configuration = getConfiguration();
        if(configuration == null) {
            return Integer.MAX_VALUE;
        }

        return configuration.getNStep();
    }

    @Override
    public NN getCurrent() {
        return current;
    }

    public void setUpdateAlgorithm(UpdateAlgorithm<NN> updateAlgorithm) {
        this.updateAlgorithm = updateAlgorithm;
    }

    public ExperienceHandler getExperienceHandler() {
        return experienceHandler;
    }

    public void setExperienceHandler(ExperienceHandler experienceHandler) {
        this.experienceHandler = experienceHandler;
    }

    // TODO: Add an actor-learner class and be able to inject the update algorithm
    protected abstract UpdateAlgorithm<NN> buildUpdateAlgorithm();

    @Override
    public void setHistoryProcessor(IHistoryProcessor historyProcessor) {
        super.setHistoryProcessor(historyProcessor);
        updateAlgorithm = buildUpdateAlgorithm();
    }

    @Override
    protected void preEpisode() {
        experienceHandler.reset();
    }


    /**
     * "Subepoch"  correspond to the t_max-step iterations
     * that stack rewards with t_max MiniTrans
     *
     * @param sObs  the obs to start from
     * @param trainingSteps the number of training steps
     * @return subepoch training informations
     */
    public SubEpochReturn trainSubEpoch(Observation sObs, int trainingSteps) {

        current.copyFrom(getAsyncGlobal().getTarget());

        Observation obs = sObs;
        IPolicy<Integer> policy = getPolicy(current);

        Integer action = getMdp().getActionSpace().noOp();

        double reward = 0;
        double accuReward = 0;

        while (!getMdp().isDone() && experienceHandler.getTrainingBatchSize() != trainingSteps) {

            //if step of training, just repeat lastAction
            if (!obs.isSkipped()) {
                action = policy.nextAction(obs);
            }

            StepReply<Observation> stepReply = getLegacyMDPWrapper().step(action);
            accuReward += stepReply.getReward() * getConfiguration().getRewardFactor();

            if (!obs.isSkipped()) {
                experienceHandler.addExperience(obs, action, accuReward, stepReply.isDone());
                accuReward = 0;

                incrementSteps();
            }

            obs = stepReply.getObservation();
            reward += stepReply.getReward();

        }

        boolean episodeComplete = getMdp().isDone() || getConfiguration().getMaxEpochStep() == currentEpisodeStepCount;

        if (episodeComplete && experienceHandler.getTrainingBatchSize() != trainingSteps) {
            experienceHandler.setFinalObservation(obs);
        }

        int experienceSize = experienceHandler.getTrainingBatchSize();

        getAsyncGlobal().applyGradient(updateAlgorithm.computeGradients(current, experienceHandler.generateTrainingBatch()), experienceSize);

        return new SubEpochReturn(experienceSize, obs, reward, current.getLatestScore(), episodeComplete);
    }
}
