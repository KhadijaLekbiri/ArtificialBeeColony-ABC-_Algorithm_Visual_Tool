package abc.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ABCAlgorithm {
    private final int dimension = 2; // for visualization: x and y
    private final double[] lower = new double[] {-50, -50};
    private final double[] upper = new double[] {50, 50};

    private int foodNumber = 20; // number of food sources (usually colony/2)
    private int limit = 50; // abandonment limit
    private List<FoodSource> foods;
    private int iteration = 0;

    // callbacks for UI to inspect state
    public interface UpdateListener {
        void onUpdate(List<FoodSource> foods, int iteration);
    }
    private UpdateListener listener;

    public ABCAlgorithm() {
        init();
    }

    public void setUpdateListener(UpdateListener l) {
        this.listener = l;
    }

    public void init() {
        foods = new ArrayList<>();
        for (int i = 0; i < foodNumber; i++) {
            foods.add(new FoodSource(dimension, lower, upper));
        }
        iteration = 0;
        notifyListener();
    }

    public void setFoodNumber(int n) {
        this.foodNumber = Math.max(1, n);
        init();
    }

    public int getIteration() {
        return iteration;
    }

    public List<FoodSource> getFoods() {
        return foods;
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onUpdate(foods, iteration);
        }
    }

    // Employed bee phase: each employed bee improves its source
    public void employedSearch(int i) {
        FoodSource current = foods.get(i);
        FoodSource candidate = current.copy();
        // generate neighbor
        int dim = current.getDimension();
        int k = i;
        while (k == i) k = (int) (Math.random() * foods.size());
        double[] xk = foods.get(k).getPosition();
        double[] newPos = candidate.getPosition().clone();
        int j = (int) (Math.random() * dim);
        double phi = (Math.random() * 2 - 1); // [-1,1]
        newPos[j] = newPos[j] + phi * (newPos[j] - xk[j]);
        // clip
        for (int d = 0; d < dim; d++) {
            if (newPos[d] < lower[d]) newPos[d] = lower[d];
            if (newPos[d] > upper[d]) newPos[d] = upper[d];
        }
        candidate.setPosition(newPos);
        // greedy selection
        if (candidate.getObjective() < current.getObjective()) {
            foods.set(i, candidate);
            candidate.resetTrial();
        } else {
            current.incrementTrial();
        }
    }

    // Onlooker bee phase: probabilistic selection
    public void onlookerSearch() {
        double sumFit = 0;
        for (FoodSource f : foods) sumFit += f.getFitness();
        // roulette wheel
        double r = Math.random() * sumFit;
        double acc = 0;
        int selected = 0;
        for (int i = 0; i < foods.size(); i++) {
            acc += foods.get(i).getFitness();
            if (acc >= r) {
                selected = i;
                break;
            }
        }
        // apply same neighborhood search to selected
        employedSearch(selected);
    }

    // Scout phase: replace abandoned sources
    public void scoutPhase() {
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).getTrial() >= limit) {
                FoodSource f = new FoodSource(dimension, lower, upper);
                foods.set(i, f);
            }
        }
    }

    public void runIteration() {
        // employed bees
        for (int i = 0; i < foods.size(); i++) {
            employedSearch(i);
        }
        // calculate probabilites implicitly by onlookerSearch sampling
        int onlookers = foods.size(); // simple choice
        for (int i = 0; i < onlookers; i++) {
            onlookerSearch();
        }
        // scout
        scoutPhase();
        iteration++;
        notifyListener();
    }

    public FoodSource getBest() {
        return foods.stream().min(Comparator.comparingDouble(FoodSource::getObjective)).orElse(null);
    }
}
