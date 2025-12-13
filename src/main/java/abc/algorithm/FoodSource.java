// src/main/java/abc/algorithm/FoodSource.java
package abc.algorithm;

public class FoodSource {
    private double x, y, fitness;
    private int trialCount;

    public FoodSource(double x, double y, double fitness, int trialCount) {
        this.x = x;
        this.y = y;
        this.fitness = fitness;
        this.trialCount = trialCount;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getFitness() { return fitness; }
    public int getTrialCount() { return trialCount; }
    public void incrementTrialCount() { this.trialCount++; }

    // Helper method for color calculation
    public double getFitnessRatio() {
        return Math.max(0, Math.min(1, fitness / 100.0));
    }
}