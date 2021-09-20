package arc.a2c;

import arc.alorg.ALOrg;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.async.IAsyncGlobal;
import org.deeplearning4j.rl4j.learning.configuration.IAsyncLearningConfiguration;
import org.deeplearning4j.rl4j.learning.listener.TrainingListener;
import org.deeplearning4j.rl4j.learning.listener.TrainingListenerList;
import org.deeplearning4j.rl4j.network.NeuralNet;
import org.deeplearning4j.rl4j.space.ActionSpace;
import org.deeplearning4j.rl4j.space.Encodable;

public abstract class NormalLearning <OBSERVATION extends Encodable, ACTION, ACTION_SPACE extends ActionSpace<ACTION>, NN extends NeuralNet> extends Learning<OBSERVATION, ACTION, ACTION_SPACE, NN> {
    private final TrainingListenerList listeners = new TrainingListenerList();
    private Learner learner;

    public void addListener(TrainingListener listener) {
        listeners.add(listener);
    }

    public abstract IAsyncLearningConfiguration getConfiguration();

    public abstract LearnerDiscrete newLearner();

    protected abstract IAsyncGlobal<NN> getAsyncGlobal();

    protected boolean isTrainingComplete() {
        return getAsyncGlobal().isTrainingComplete();
    }

    private boolean canContinue = true;

    private int progressMonitorFrequency = 20000;

    @Override
    public int getStepCount() {
        return getAsyncGlobal().getStepCount();
    }

    public TrainingListenerList getListeners() {
        return listeners;
    }

    public void init() {
        ALOrg.LOGGER.info("Learning started...");
        canContinue = listeners.notifyTrainingStarted();
        if (canContinue) {
            learner = newLearner();
            learner.run();
        }
    }

    public void train() {
        if (canContinue) {
            learner.step();
            monitorTraining();
        }

        if (!canContinue) {
            listeners.notifyTrainingFinished();
        }
    }

    protected void monitorTraining() {
        if (canContinue && !isTrainingComplete()) {
            canContinue = listeners.notifyTrainingProgress(this);
            if (!canContinue) {
                return;
            }
        }
    }

    public void terminate() {
        if (canContinue) {
            canContinue = false;
        }
    }
}
