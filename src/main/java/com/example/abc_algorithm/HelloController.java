package com.example.abc_algorithm;

import com.example.abc_algorithm.algo.ABCAlgorithm;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloController {


    @FXML
    private Canvas canvas;
    private ABCAlgorithm abc;

    private final List<Bee> bees = new ArrayList<>();
    private final List<FoodSource> foodSources = new ArrayList<>();
    private AnimationTimer timer;
    private final Random random = new Random();

    @FXML
    public void initialize() {
        // Create food sources at random positions
        for (int i = 0; i < 5; i++) {
            foodSources.add(new FoodSource(random.nextDouble() * canvas.getWidth(),
                    random.nextDouble() * canvas.getHeight()));
        }

        // Create bees with roles
        for (int i = 0; i < 30; i++) {
            Bee.Role role = (i < 10) ? Bee.Role.EMPLOYED : (i < 25) ? Bee.Role.ONLOOKER : Bee.Role.SCOUT;
            bees.add(new Bee(random.nextDouble() * canvas.getWidth(),
                    random.nextDouble() * canvas.getHeight(), role));

        }
        abc = new ABCAlgorithm(5,                 // number of food sources
                2,                 // dimension (x, y)
                canvas.getWidth(), // maxBound for x
                canvas.getHeight() // maxBound for y
        );
    }

    @FXML
    public void startVisualization() {
        if (timer != null) return;

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateBees();
                drawScene();
            }
        };
        timer.start();
    }

    private void updateBees() {
        for (Bee b : bees) {
            b.x += random.nextDouble() * 4 - 2;
            b.y += random.nextDouble() * 4 - 2;

            // Keep inside canvas
            if (b.x < 0) b.x = 0;
            if (b.y < 0) b.y = 0;
            if (b.x > canvas.getWidth()) b.x = canvas.getWidth();
            if (b.y > canvas.getHeight()) b.y = canvas.getHeight();
        }
    }

    private void drawScene() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Clear canvas
        gc.setFill(Color.LIGHTYELLOW);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw food sources
        gc.setFill(Color.ORANGE);
        for (FoodSource fs : foodSources) {
            gc.fillOval(fs.x - 7.5, fs.y - 7.5, 15, 15);
        }

        // Draw bees with color based on role
        for (Bee b : bees) {
            switch (b.role) {
                case EMPLOYED -> gc.setFill(Color.BLUE);
                case ONLOOKER -> gc.setFill(Color.GREEN);
                case SCOUT -> gc.setFill(Color.RED);
            }
            gc.fillOval(b.x - 5, b.y - 5, 10, 10);
        }

        // Draw legend/key
        double keyX = canvas.getWidth() - 150;
        double keyY = 50;
        gc.setFill(Color.BLACK);
        gc.fillText("Legend:", keyX, keyY);

        gc.setFill(Color.BLUE);
        gc.fillRect(keyX, keyY + 10, 15, 15);
        gc.setFill(Color.BLACK);
        gc.fillText("Employed Bee", keyX + 20, keyY + 23);

        gc.setFill(Color.GREEN);
        gc.fillRect(keyX, keyY + 35, 15, 15);
        gc.setFill(Color.BLACK);
        gc.fillText("Onlooker Bee", keyX + 20, keyY + 48);

        gc.setFill(Color.RED);
        gc.fillRect(keyX, keyY + 60, 15, 15);
        gc.setFill(Color.BLACK);
        gc.fillText("Scout Bee", keyX + 20, keyY + 73);

        gc.setFill(Color.ORANGE);
        gc.fillRect(keyX, keyY + 85, 15, 15);
        gc.setFill(Color.BLACK);
        gc.fillText("Flower (Food)", keyX + 20, keyY + 98);
    }

    public static class Bee {
        double x, y;
        Role role;
        public Bee(double x, double y, Role role) { this.x = x; this.y = y; this.role = role; }
        public enum Role { EMPLOYED, ONLOOKER, SCOUT }
    }

    public static class FoodSource {
        double x, y;
        public FoodSource(double x, double y) { this.x = x; this.y = y; }
    }
}
