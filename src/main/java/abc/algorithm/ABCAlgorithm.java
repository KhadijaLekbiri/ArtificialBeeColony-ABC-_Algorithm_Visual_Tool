// src/main/java/abc/algorithm/ABCAlgorithm.java
package abc.algorithm;

import java.util.*;

public class ABCAlgorithm {
    private List<FoodSource> foodSources;
    private final int maxIterations = 1000;
    private final int limit;
    private final double neighborhoodRadius;
    private int currentIteration = 0;
    private final Random rand = new Random();
    private String currentPhase = "Initialization";

    public ABCAlgorithm(int foodSourceCount, int limit, double neighborhoodRadius) {
        this.limit = limit;
        this.neighborhoodRadius = neighborhoodRadius;
        this.foodSources = new ArrayList<>();
        for (int i = 0; i < foodSourceCount; i++) {
            double x = rand.nextDouble();
            double y = rand.nextDouble();
            double fitness = calculateFitness(x, y);
            foodSources.add(new FoodSource(x, y, fitness, 0));
        }
        this.currentPhase = "Initialization Complete";
    }

    public void runIteration() {
        currentIteration++;

        // --- EMPLOYED BEE PHASE ---
        currentPhase = "Employed Bee Phase";
        for (int i = 0; i < foodSources.size(); i++) {
            FoodSource current = foodSources.get(i);
            double newX = current.getX() + (rand.nextDouble() * 2 - 1) * neighborhoodRadius;
            double newY = current.getY() + (rand.nextDouble() * 2 - 1) * neighborhoodRadius;
            newX = Math.max(0, Math.min(1, newX));
            newY = Math.max(0, Math.min(1, newY));
            double newFitness = calculateFitness(newX, newY);

            if (newFitness > current.getFitness()) {
                foodSources.set(i, new FoodSource(newX, newY, newFitness, 0));
            } else {
                current.incrementTrialCount();
            }
        }

        // --- ONLOOKER BEE PHASE ---
        currentPhase = "Onlooker Bee Phase";
        double totalFitness = foodSources.stream().mapToDouble(FoodSource::getFitness).sum();
        int onlookers = foodSources.size();

        for (int i = 0; i < onlookers; i++) {
            double pick = rand.nextDouble() * totalFitness;
            double currentSum = 0;
            int selectedIndex = -1;

            for (int j = 0; j < foodSources.size(); j++) {
                currentSum += foodSources.get(j).getFitness();
                if (currentSum >= pick) {
                    selectedIndex = j;
                    break;
                }
            }

            if (selectedIndex != -1) {
                FoodSource selected = foodSources.get(selectedIndex);
                double newX = selected.getX() + (rand.nextDouble() * 2 - 1) * neighborhoodRadius;
                double newY = selected.getY() + (rand.nextDouble() * 2 - 1) * neighborhoodRadius;
                newX = Math.max(0, Math.min(1, newX));
                newY = Math.max(0, Math.min(1, newY));
                double newFitness = calculateFitness(newX, newY);

                if (newFitness > selected.getFitness()) {
                    foodSources.set(selectedIndex, new FoodSource(newX, newY, newFitness, 0));
                } else {
                    selected.incrementTrialCount();
                }
            }
        }

        // --- SCOUT BEE PHASE ---
        currentPhase = "Scout Bee Phase";
        List<FoodSource> toRemove = new ArrayList<>();
        List<FoodSource> toAdd = new ArrayList<>();

        for (FoodSource fs : foodSources) {
            if (fs.getTrialCount() > limit) {
                toRemove.add(fs);
                double x = rand.nextDouble();
                double y = rand.nextDouble();
                double fitness = calculateFitness(x, y);
                toAdd.add(new FoodSource(x, y, fitness, 0));
            }
        }

        foodSources.removeAll(toRemove);
        foodSources.addAll(toAdd);

        currentPhase = "Iteration " + currentIteration + " Complete";
    }

    public double calculateFitness(double x, double y) {
        double nx = x * 20 - 10;
        double ny = y * 20 - 10;
        double rastrigin = 20 +
                (nx * nx - 10 * Math.cos(2 * Math.PI * nx)) +
                (ny * ny - 10 * Math.cos(2 * Math.PI * ny));
        return Math.max(0, 100 - Math.min(100, rastrigin));
    }

    public boolean isConverged() {
        if (foodSources.size() < 2) return true;
        FoodSource best = Collections.max(foodSources, (a, b) -> Double.compare(a.getFitness(), b.getFitness()));
        double maxDist = 0;
        for (FoodSource fs : foodSources) {
            double dx = fs.getX() - best.getX();
            double dy = fs.getY() - best.getY();
            double dist = Math.sqrt(dx*dx + dy*dy);
            if (dist > maxDist) maxDist = dist;
        }
        return maxDist < 0.05;
    }

    // Analytics
    public double getBestFitness() {
        return foodSources.stream().mapToDouble(FoodSource::getFitness).max().orElse(0);
    }

    public double getAverageFitness() {
        return foodSources.stream().mapToDouble(FoodSource::getFitness).average().orElse(0);
    }

    public int getAbandonedCount() {
        return (int) foodSources.stream().filter(fs -> fs.getTrialCount() > limit * 0.9).count();
    }

    public int getScoutCount() {
        return (int) foodSources.stream().filter(fs -> fs.getTrialCount() > limit).count();
    }

    // Getters
    public List<FoodSource> getFoodSources() { return foodSources; }
    public int getCurrentIteration() { return currentIteration; }
    public int getMaxIterations() { return maxIterations; }
    public int getLimit() { return limit; }
    public double getNeighborhoodRadius() { return neighborhoodRadius; }
    public String getCurrentPhase() { return currentPhase; }
}