package abc.algorithm;

import java.util.Arrays;

public class FoodSource {
    private double[] position;
    private double fitness; // higher is better (we convert objective -> fitness)
    private double objective; // raw objective (to minimize)
    private int trial; // number of failed improvements

    private final double[] lower;
    private final double[] upper;

    public FoodSource(int dim, double[] lower, double[] upper) {
        this.position = new double[dim];
        this.lower = Arrays.copyOf(lower, dim);
        this.upper = Arrays.copyOf(upper, dim);
        randomize();
        evaluate();
        this.trial = 0;
    }

    public FoodSource(double[] pos, double[] lower, double[] upper) {
        this.position = Arrays.copyOf(pos, pos.length);
        this.lower = Arrays.copyOf(lower, pos.length);
        this.upper = Arrays.copyOf(upper, pos.length);
        evaluate();
        this.trial = 0;
    }

    public void randomize() {
        for (int i = 0; i < position.length; i++) {
            position[i] = lower[i] + Math.random() * (upper[i] - lower[i]);
        }
        evaluate();
    }

    public double[] getPosition() {
        return position;
    }

    public double getObjective() {
        return objective;
    }

    public double getFitness() {
        return fitness;
    }

    public int getTrial() {
        return trial;
    }

    public void incrementTrial() {
        trial++;
    }

    public void resetTrial() {
        trial = 0;
    }

    public void setPosition(double[] pos) {
        this.position = Arrays.copyOf(pos, pos.length);
        evaluate();
    }

    public int getDimension() {
        return position.length;
    }

    public void evaluate() {
        // default objective: Sphere function (minimize) -- sum(x_i^2)
        double sum = 0;
        for (double v : position) sum += v * v;
        objective = sum;
        // convert to fitness: higher is better
        fitness = 1.0 / (1.0 + objective);
    }

    public FoodSource copy() {
        FoodSource f = new FoodSource(this.position, this.lower, this.upper);
        f.trial = this.trial;
        return f;
    }
}
