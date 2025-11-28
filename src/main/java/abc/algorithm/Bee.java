package abc.algorithm;

public abstract class Bee {
    protected ABCAlgorithm algorithm;

    public Bee(ABCAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public abstract void act();
}
