package com.example.abc_algorithm.algo;

public class Bee {

    private double[] position;   // instead of x, y
    private Role role;
    private FoodSource currentFood;

    public enum Role { EMPLOYED, ONLOOKER, SCOUT }

    public Bee(FoodSource food, double[] position, Role role) {
        this.currentFood = food;
        this.position = position.clone(); // defensive copy
        this.role = role;
    }

    // --- FOOD SOURCE ---
    public FoodSource getCurrentFood() {
        return currentFood;
    }

    public void setCurrentFood(FoodSource food) {
        this.currentFood = food;
    }

    // --- POSITION ---
    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] newPos) {
        this.position = newPos.clone();
    }


    public void setCoordinate(int i, double value) {
        this.position[i] = value;
    }

    // Optional: for visualization or movement
    public void moveTo(double[] newPosition) {
        this.position = newPosition.clone();
    }

    // --- ROLE ---
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
