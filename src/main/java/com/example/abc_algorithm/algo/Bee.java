package com.example.abc_algorithm.algo;

public class Bee {
    private FoodSource currentFood;

    public Bee(FoodSource food) {
        this.currentFood = food;
    }

    public FoodSource getCurrentFood() {
        return currentFood;
    }

    public void setCurrentFood(FoodSource food) {
        this.currentFood = food;
    }
}
