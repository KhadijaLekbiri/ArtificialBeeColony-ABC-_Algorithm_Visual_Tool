package com.example.abc_algorithm.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ABCAlgorithm {
    
    private int foodCount;
    private int dimension;
    private double minBound;
    private double maxBound;

    private List<FoodSource> foods;
    private Random rand = new Random();

    public ABCAlgorithm(int foodCount, int dimension,
                        double minBound, double maxBound) {

        this.foodCount = foodCount;
        this.dimension = dimension;
        this.minBound = minBound;
        this.maxBound = maxBound;
        this.foods = new ArrayList<>();

        initializeFoodSources();
    }

    // ------------------------------------------------------
    // INITIALIZATION
    // ------------------------------------------------------
    private void initializeFoodSources() {
        for (int i = 0; i < foodCount; i++) {
            double[] pos = new double[dimension];
            for (int d = 0; d < dimension; d++) {
                pos[d] = Utils.random(minBound, maxBound);
            }
            double fit = Utils.fitness(pos);
            foods.add(new FoodSource(pos, fit));
        }
    }

    // ------------------------------------------------------
    // ONE CYCLE OF THE ALGORITHM
    // ------------------------------------------------------
    public void runOneCycle() {
        employedBeePhase();
        onlookerBeePhase();
        scoutBeePhase();
    }

    // PHASE 1: Employed bees
    private void employedBeePhase() {
        for (int i = 0; i < foodCount; i++) {

            FoodSource food = foods.get(i);
            double[] x = food.getPosition();

            // choose a random different food index k
            int k;
            do {
                k = rand.nextInt(foodCount);
            } while (k == i);

            FoodSource partner = foods.get(k);
            double[] xk = partner.getPosition();

            // generate new candidate solution
            double[] newPos = x.clone();
            int dim = rand.nextInt(dimension);        // random dimension to modify
            double phi = Utils.random(-1, 1);         // phi in [-1,1]

            newPos[dim] = x[dim] + phi * (x[dim] - xk[dim]);
            newPos[dim] = Utils.clamp(newPos[dim], minBound, maxBound);

            double newFit = Utils.fitness(newPos);

            // greedy selection
            if (newFit > food.getFitness()) {
                food.setPosition(newPos);
                food.setFitness(newFit);
                food.resetTrials();
            } else {
                food.incrementTrials();
            }
        }
    }


    // PHASE 2: Onlooker bees
    private void onlookerBeePhase() {
        double sumFitness = foods.stream()
                .mapToDouble(f -> Math.abs(f.getFitness()))
                .sum();

        int onlookers = foodCount;

        int count = 0;
        while (count < onlookers) {

            for (int i = 0; i < foodCount && count < onlookers; i++) {

                FoodSource food = foods.get(i);
                double probability = Math.abs(food.getFitness()) / sumFitness;

                if (rand.nextDouble() < probability) {

                    // onlooker bee does the same neighborhood search
                    count++;

                    double[] x = food.getPosition();

                    int k;
                    do {
                        k = rand.nextInt(foodCount);
                    } while (k == i);

                    double[] xk = foods.get(k).getPosition();
                    double[] newPos = x.clone();

                    int dim = rand.nextInt(dimension);
                    double phi = Utils.random(-1, 1);

                    newPos[dim] = x[dim] + phi * (x[dim] - xk[dim]);
                    newPos[dim] = Utils.clamp(newPos[dim], minBound, maxBound);

                    double newFit = Utils.fitness(newPos);

                    if (newFit > food.getFitness()) {
                        food.setPosition(newPos);
                        food.setFitness(newFit);
                        food.resetTrials();
                    } else {
                        food.incrementTrials();
                    }
                }
            }
        }
    }


    // PHASE 3: Scout bees
    private void scoutBeePhase() {
        for (FoodSource food : foods) {
            if (food.getTrialCounter() > 50) {

                double[] newPos = new double[dimension];
                for (int d = 0; d < dimension; d++) {
                    newPos[d] = Utils.random(minBound, maxBound);
                }

                food.setPosition(newPos);
                food.setFitness(Utils.fitness(newPos));
                food.resetTrials();
            }
        }
    }


    // ------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------
    public List<FoodSource> getFoodSources() {
        return foods;
    }

    public FoodSource getBestFoodSource() {
        return foods.stream()
                .max((a, b) -> Double.compare(a.getFitness(), b.getFitness()))
                .orElse(null);
    }
}
