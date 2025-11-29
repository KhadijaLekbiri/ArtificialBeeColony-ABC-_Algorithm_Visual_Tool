package com.example.abc_algorithm.algo;

import java.util.Random;

public class Utils {
    private static final Random rand = new Random();

    // random double between min and max
    public static double random(double min, double max) {
        return min + (max - min) * rand.nextDouble();
    }

    // clamp values inside boundaries
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // example fitness function (you will replace this)
    public static double fitness(double[] x) {
        // Sphere function
        double sum = 0;
        for (double xi : x) sum += xi * xi;
        return -sum; // maximize => return negative
    }
}
