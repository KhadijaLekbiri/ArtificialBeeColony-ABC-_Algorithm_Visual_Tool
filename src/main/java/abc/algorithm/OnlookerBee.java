package abc.algorithm;

public class OnlookerBee extends Bee {

    public OnlookerBee(ABCAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    public void act() {
        algorithm.onlookerSearch();
    }
}
