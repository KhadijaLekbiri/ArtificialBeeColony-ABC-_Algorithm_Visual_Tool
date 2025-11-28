package abc.algorithm;

public class ScoutBee extends Bee {

    public ScoutBee(ABCAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    public void act() {
        algorithm.scoutPhase();
    }
}
