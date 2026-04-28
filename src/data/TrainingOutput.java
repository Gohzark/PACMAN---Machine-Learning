package data;
import java.util.ArrayList;
import neuralNetwork.*;

public class TrainingOutput {
    public ArrayList<TrainExample> examples;
    public float score;

    public TrainingOutput(ArrayList<TrainExample> e, float s) {
        this.examples = e;
        this.score = s;
    }

    public ArrayList<TrainExample> getExamples() { return examples; }
    public float getAverageReward() { return score; }
}