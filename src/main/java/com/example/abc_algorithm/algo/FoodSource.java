package com.example.abc_algorithm.algo;

import java.util.Arrays;

public class FoodSource {
    private double[] position;
    private double fitness;
    private int trialCounter;

    public FoodSource(double[] position, double fitness) {
        this.position = position;
        this.fitness = fitness;
        this.trialCounter = 0;
    }

    public double[] getPosition() {
        return position;
    }

    public double get(int i){
        return position[i];
    }
    public double getFitness() {
        return fitness;
    }

    public int getTrialCounter() {
        return trialCounter;
    }

    public void setPosition(double[] newPos) {
        this.position = newPos;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void incrementTrials() {
        this.trialCounter++;
    }

    public void resetTrials() {
        this.trialCounter = 0;
    }

    @Override
    public String toString() {
        return "FoodSource{" +
                "position=" + Arrays.toString(position) +
                ", fitness=" + fitness +
                ", trials=" + trialCounter +
                '}';
    }
}
