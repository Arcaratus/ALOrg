package arc.a2c;

import arc.alorg.ALOrg;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.IEpochTrainer;
import org.deeplearning4j.rl4j.learning.IHistoryProcessor;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.async.IAsyncGlobal;
import org.deeplearning4j.rl4j.learning.configuration.IAsyncLearningConfiguration;
import org.deeplearning4j.rl4j.learning.listener.TrainingListenerList;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.NeuralNet;
import org.deeplearning4j.rl4j.observation.Observation;
import org.deeplearning4j.rl4j.policy.IPolicy;
import org.deeplearning4j.rl4j.space.ActionSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.util.IDataManager;
import org.deeplearning4j.rl4j.util.LegacyMDPWrapper;

public abstract class Learner<OBSERVATION extends Encodable, ACTION, ACTION_SPACE extends ActionSpace<ACTION>, NN extends NeuralNet> implements IEpochTrainer {
    protected int stepCount = 0;
    protected int epochCount = 0;
    protected int episodeCount = 0;
    protected int currentEpisodeStepCount = 0;
    boolean episodeComplete = true;
    private IHistoryProcessor historyProcessor;

    private RunContext context;
    private boolean isEpisodeStarted = false;
    private final LegacyMDPWrapper<OBSERVATION, ACTION, ACTION_SPACE> mdp;

    private final TrainingListenerList listeners;

    public Learner(MDP<OBSERVATION, ACTION, ACTION_SPACE> mdp, TrainingListenerList listeners) {
        this.mdp = new LegacyMDPWrapper<>(mdp, null);
        this.listeners = listeners;
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    @Override
    public int getEpochCount() {
        return epochCount;
    }

    @Override
    public int getEpisodeCount() {
        return episodeCount;
    }

    @Override
    public int getCurrentEpisodeStepCount() {
        return currentEpisodeStepCount;
    }

    @Override
    public IHistoryProcessor getHistoryProcessor() {
        return historyProcessor;
    }

    public MDP<OBSERVATION, ACTION, ACTION_SPACE> getMdp() {
        return mdp.getWrappedMDP();
    }
    protected LegacyMDPWrapper<OBSERVATION, ACTION, ACTION_SPACE> getLegacyMDPWrapper() {
        return mdp;
    }

    public void setHistoryProcessor(IHistoryProcessor.Configuration conf) {
        setHistoryProcessor(new HistoryProcessor(conf));
    }

    public void setHistoryProcessor(IHistoryProcessor historyProcessor) {
        this.historyProcessor = historyProcessor;
        mdp.setHistoryProcessor(historyProcessor);
    }

    protected void postEpisode() {
        if (getHistoryProcessor() != null)
            getHistoryProcessor().stopMonitor();

    }

    protected void preEpisode() {
        // Do nothing
    }

    public void run() {
        context = new RunContext();

        ALOrg.LOGGER.info("Learner started!");

//        while (!getAsyncGlobal().isTrainingComplete()) {

//            episodeComplete = handleTraining(context);
//
//            if (!finishEpoch(context)) {
//                return;
//            }
//
//            if (episodeComplete) {
//                finishEpisode(context);
//            }
//        }
    }

    public void step() {
        if (episodeComplete) {
            startEpisode(context);
        }

        if (!startEpoch(context)) {
            return;
        }

        episodeComplete = handleTraining(context);

        if (!finishEpoch(context)) {
            return;
        }

        if (episodeComplete) {
            finishEpisode(context);
        }
    }

    private boolean finishEpoch(RunContext context) {
        epochCount++;
        IDataManager.StatEntry statEntry = new StatEntry(stepCount, epochCount, context.rewards, currentEpisodeStepCount, context.score);
        return listeners.notifyEpochTrainingResult(this, statEntry);
    }

    private boolean startEpoch(RunContext context) {
        return listeners.notifyNewEpoch(this);
    }

    private boolean handleTraining(RunContext context) {
        int maxTrainSteps = Math.min(getConfiguration().getNStep(), getConfiguration().getMaxEpochStep() - currentEpisodeStepCount);
        SubEpochReturn subEpochReturn = trainSubEpoch(context.obs, maxTrainSteps);

        context.obs = subEpochReturn.getLastObs();
        context.rewards += subEpochReturn.getReward();
        context.score = subEpochReturn.getScore();

        return subEpochReturn.isEpisodeComplete();
    }

    private void startEpisode(RunContext context) {
        getCurrent().reset();
        Learning.InitMdp<Observation> initMdp = refacInitMdp();

        context.obs = initMdp.getLastObs();
        context.rewards = initMdp.getReward();

        preEpisode();
        episodeCount++;
    }

    private void finishEpisode(RunContext context) {
        postEpisode();

        ALOrg.LOGGER.info("Episode step: " + currentEpisodeStepCount + ", Episode: " + episodeCount + ", Epoch: " + epochCount + ", reward: " + context.rewards);
    }

    protected abstract NN getCurrent();

    protected abstract IAsyncGlobal<NN> getAsyncGlobal();

    protected abstract IAsyncLearningConfiguration getConfiguration();

    protected abstract IPolicy<ACTION> getPolicy(NN net);

    protected abstract SubEpochReturn trainSubEpoch(Observation obs, int nstep);

    private Learning.InitMdp<Observation> refacInitMdp() {
        currentEpisodeStepCount = 0;

        double reward = 0;

        LegacyMDPWrapper<OBSERVATION, ACTION, ACTION_SPACE> mdp = getLegacyMDPWrapper();
        Observation observation = mdp.reset();

        ACTION action = mdp.getActionSpace().noOp(); //by convention should be the NO_OP
        while (observation.isSkipped() && !mdp.isDone()) {
            StepReply<Observation> stepReply = mdp.step(action);

            reward += stepReply.getReward();
            observation = stepReply.getObservation();

            incrementSteps();
        }

        return new Learning.InitMdp(0, observation, reward);

    }

    public void incrementSteps() {
        stepCount++;
        currentEpisodeStepCount++;
    }

    public static class SubEpochReturn {
        int steps;
        Observation lastObs;
        double reward;
        double score;
        boolean episodeComplete;

        public SubEpochReturn(int steps, Observation lastObs, double reward, double score, boolean episodeComplete) {
            this.steps = steps;
            this.lastObs = lastObs;
            this.reward = reward;
            this.score = score;
            this.episodeComplete = episodeComplete;
        }

        public int getSteps() {
            return steps;
        }

        public Observation getLastObs() {
            return lastObs;
        }

        public double getReward() {
            return reward;
        }

        public double getScore() {
            return score;
        }

        public boolean isEpisodeComplete() {
            return episodeComplete;
        }
    }

    public static class StatEntry implements IDataManager.StatEntry {
        int stepCounter;
        int epochCounter;
        double reward;
        int episodeLength;
        double score;

        public StatEntry(int stepCounter, int epochCounter, double reward, int episodeLength, double score) {
            this.stepCounter = stepCounter;
            this.epochCounter = epochCounter;
            this.reward = reward;
            this.episodeLength = episodeLength;
            this.score = score;
        }

        @Override
        public int getStepCounter() {
            return stepCounter;
        }

        @Override
        public int getEpochCounter() {
            return epochCounter;
        }

        @Override
        public double getReward() {
            return reward;
        }

        public int getEpisodeLength() {
            return episodeLength;
        }

        public double getScore() {
            return score;
        }
    }

    private static class RunContext {
        private Observation obs;
        private double rewards;
        private double score;
    }
}
