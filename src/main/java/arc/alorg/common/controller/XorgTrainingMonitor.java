package arc.alorg.common.controller;

import arc.alorg.ALOrg;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XorgTrainingMonitor {
    private static final Map<Integer, XorgTrainingMonitor> monitors = new HashMap<>();
    private static final String FILE_PREFIX = "stats";

    private final int id;

    private int epochs;
    private int episodes;
    private double rewards;
    private double time;

    private double currentDistance;

    private double completionSpeeds;

    private double avgEpisodes;
    private double avgReward;
    private double avgTime;
    private double avgCompletionSpeed;

    public XorgTrainingMonitor(int id) {
        this.id = id;

        epochs = 0;
        episodes = 0;
        rewards = 0;
        time = 0;
        currentDistance = 0;
        completionSpeeds = 0;
    }

    public void update(int episodes, double reward, double time) {
        epochs++;
        this.episodes += episodes;
        rewards += reward;
        this.time += time;
        completionSpeeds += currentDistance / time;

        avgEpisodes = (double) this.episodes / epochs;
        avgReward = rewards / epochs;
        avgTime = this.time / epochs;
        avgCompletionSpeed = completionSpeeds / epochs;
    }

    public void writeToJSON(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            mapper.writeValue(new File(filePath + FILE_PREFIX + id), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initMonitor(int id, double distance) {
        XorgTrainingMonitor monitor = monitors.getOrDefault(id, new XorgTrainingMonitor(id));
        monitor.currentDistance = distance;
        monitors.put(id, monitor);
    }

    public static void updateMonitor(int id, int episodes, double reward, double time) {
        XorgTrainingMonitor monitor = monitors.getOrDefault(id, new XorgTrainingMonitor(id));
        monitor.update(episodes, reward, time);
        monitor.writeToJSON(Controller.MODEL_SAVES);
        monitors.put(id, monitor);
        ALOrg.LOGGER.info(monitor);
    }

    @Override
    public String toString() {
        return "Monitor " + id + ": {Epochs: " + epochs + ", AvgEpisodes: " + avgEpisodes + ", AvgReward: " + avgReward + ", AvgTime: " + avgTime + ", AvgCompletion: " + avgCompletionSpeed + "}";
    }
}
