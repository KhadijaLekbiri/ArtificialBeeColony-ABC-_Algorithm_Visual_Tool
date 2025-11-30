package com.example.abc_algorithm.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ABCAlgorithm {
    
    private int foodCount;
    private int dimension;
    private double minBound;
    private double maxBound;

    private List<FoodSource> foodSources;
    private Random rand = new Random();

    public ABCAlgorithm(int foodCount, int dimension,
                        double minBound, double maxBound) {

        this.foodCount = foodCount;
        this.dimension = dimension;
        this.minBound = minBound;
        this.maxBound = maxBound;
        this.foodSources = new ArrayList<>();

        initializeFoodSources();
    }

    public double getMaxBound() {
        return maxBound;
    }

    public double getMinBound() {
        return minBound;
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
            foodSources.add(new FoodSource(pos, fit));
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

    public FoodSource getRandomFoodSource(){
        return foodSources.get(rand.nextInt(foodSources.size()));
    }
    // PHASE 1: Employed bees
    public void employedBeePhase() {
        for (int i = 0; i < foodCount; i++) {

            FoodSource food = foodSources.get(i);
            double[] x = food.getPosition();

            // choose a random different food index k
            int k;
            do {
                k = rand.nextInt(foodCount);
            } while (k == i);

            FoodSource partner = foodSources.get(k);
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
    public void onlookerBeePhase() {
        double sumFitness = foodSources.stream()
                .mapToDouble(f -> Math.abs(f.getFitness()))
                .sum();

        int onlookers = foodCount;

        int count = 0;
        while (count < onlookers) {

            for (int i = 0; i < foodCount && count < onlookers; i++) {

                FoodSource food = foodSources.get(i);
                double probability = food.getFitness() / sumFitness;

                if (rand.nextDouble() < probability) {

                    // onlooker bee does the same neighborhood search
                    count++;

                    double[] x = food.getPosition();

                    int k;
                    do {
                        k = rand.nextInt(foodCount);
                    } while (k == i);

                    double[] xk = foodSources.get(k).getPosition();
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
    public void scoutBeePhase() {
        for (FoodSource food : foodSources) {
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
        return foodSources;
    }

    public FoodSource getBestFoodSource() {
        return foodSources.stream()
                .max((a, b) -> Double.compare(a.getFitness(), b.getFitness()))
                .orElse(null);
    }
}
