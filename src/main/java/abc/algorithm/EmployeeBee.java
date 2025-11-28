package abc.algorithm;

public class EmployeeBee extends Bee {
    private int index; // which food source this bee is responsible for

    public EmployeeBee(ABCAlgorithm algorithm, int index) {
        super(algorithm);
        this.index = index;
    }

    @Override
    public void act() {
        algorithm.employedSearch(index);
    }
}
