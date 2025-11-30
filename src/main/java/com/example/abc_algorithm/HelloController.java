package com.example.abc_algorithm;

import com.example.abc_algorithm.algo.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloController {

    @FXML
    private Canvas canvas;

    private ABCAlgorithm abc;
    private AnimationTimer timer;

    // visual bees (agents)
    private List<Bee> bees = new ArrayList<>();
    @FXML
    private TableView<FoodSource> foodTable;

    @FXML
    private TableColumn<FoodSource, String> colId;
    @FXML
    private TableColumn<FoodSource, Double> colX;
    @FXML
    private TableColumn<FoodSource, Double> colY;
    @FXML
    private TableColumn<FoodSource, Double> colFitness;

    @FXML
    public void initialize() {
        abc = new ABCAlgorithm(5, 2, -5, 5);
        createVisualBees();

        // Set up table columns
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(abc.getFoodSources().indexOf(cell.getValue()) + 1)
        ));
        colX.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().get(0)).asObject());
        colY.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().get(1)).asObject());
        colFitness.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getFitness()).asObject());

        foodTable.getItems().setAll(abc.getFoodSources());
    }

//    @FXML
//    public void startVisualization() {
//        timer = new AnimationTimer() {
//
//            private long lastUpdate = 0;
//            private final long delay = 200_000_000; // 200 ms = 0.2 sec → slow motion
//
//            @Override
//            public void handle(long now) {
//
//                if (now - lastUpdate < delay) {
//                    return; // skip this frame
//                }
//                lastUpdate = now;
//
//                // 1. Run the ABC algorithm
//                abc.runOneCycle();
//
//                // 2. Update visual bees
//                updateBeeMovement();
//
//                // 3. Draw
//                drawScene();
//            }
//        };
//
//        timer.start();
//    }

    @FXML
    public void startVisualization() {
        timer = new AnimationTimer() {

            private long lastUpdate = 0;
            private final long delay = 200_000_000; // 200 ms = 0.2 sec → slow motion

            @Override
            public void handle(long now) {

                if (now - lastUpdate < delay) {
                    return; // skip this frame
                }
                lastUpdate = now;

                updateAlgorithmPhase();

            }
        };

        timer.start();
    }
    private int phaseCounter = 0;

    private void updateAlgorithmPhase() {
        switch (phaseCounter) {
            case 0 -> abc.employedBeePhase();
            case 1 -> abc.onlookerBeePhase();
            case 2 -> abc.scoutBeePhase();
        }

        // move bees, draw scene
        updateBeeMovement();
        drawScene();
        foodTable.refresh();
        FoodSource best = abc.getBestFoodSource();
        if (best != null) foodTable.scrollTo(best);

        phaseCounter = (phaseCounter + 1) % 3; // loop through phases
    }


    private void createVisualBees() {
        bees.clear();
        Random r = new Random();

        // EMPLOYED BEES — random start, each linked to a food source
        for (FoodSource fs : abc.getFoodSources()) {

            double startX = r.nextDouble() * canvas.getWidth();
            double startY = r.nextDouble() * canvas.getHeight();

            bees.add(new Bee(
                    fs,                 // food source they represent
                    new double[]{startX, startY},
                    Bee.Role.EMPLOYED
            ));
        }

        // ONLOOKER BEES — random position, random food by probability
        for (int i = 0; i < 10; i++) {
            FoodSource fs = abc.getRandomFoodSource();

            double startX = r.nextDouble() * canvas.getWidth();
            double startY = r.nextDouble() * canvas.getHeight();

            bees.add(new Bee(
                    fs,
                    new double[]{startX, startY},
                    Bee.Role.ONLOOKER
            ));
        }

        // SCOUT — random wandering bee
        bees.add(new Bee(
                null,
                new double[]{
                        r.nextDouble() * canvas.getWidth(),
                        r.nextDouble() * canvas.getHeight()
                },
                Bee.Role.SCOUT
        ));
    }


    private void updateBeeMovement() {
        for (Bee b : bees) {

            if (b.getRole() == Bee.Role.SCOUT) {
                // random
                continue;
            }

            FoodSource fs = b.getCurrentFood();
            if (fs == null) continue;

            double targetX = mapToCanvas(fs.get(0));
            double targetY = mapToCanvas(fs.get(1));

            double[] pos = b.getPosition();

            double newX = pos[0] + (targetX - pos[0]) * 0.2;
            double newY = pos[1] + (targetY - pos[1]) * 0.2;

            b.setPosition(new double[]{newX, newY});
        }
    }

    private double mapToCanvas(double value) {
        double min = abc.getMinBound();
        double max = abc.getMaxBound();

        double canvasWidth = canvas.getWidth();
        // normalize
        return (value - min) / (max - min) * canvasWidth;
    }

    private void drawScene() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Clear canvas
        gc.setFill(Color.LIGHTYELLOW);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw food sources
        gc.setFill(Color.ORANGE);
        for (FoodSource fs : abc.getFoodSources()) {
            double px = mapToCanvas(fs.get(0));
            double py = mapToCanvas(fs.get(1));
            gc.fillOval(px - 7.5, py - 7.5, 15, 15);
        }

        // Draw bees with color based on role
        for (Bee b : bees) {
            switch (b.getRole()) {
                case EMPLOYED -> gc.setFill(Color.BLUE);
                case ONLOOKER -> gc.setFill(Color.GREEN);
                case SCOUT -> gc.setFill(Color.RED);
            }
            double x = b.getPosition()[0];
            double y = b.getPosition()[1];

            gc.fillOval(x - 5, y - 5, 10, 10);
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
//
//    private void updateBeeMovement() {
//        for (Bee b : bees) {
//
//            if (b.getRole() == Bee.Role.SCOUT) {
//                // random movement
//                b.setPosition(new double[]{
//                        Math.random() * canvas.getWidth(),
//                        Math.random() * canvas.getHeight()
//                });
//                continue;
//            }
//
//            FoodSource fs = b.getCurrentFood();
//            if (fs == null) continue;
//
//            // Smooth movement toward the food source
//            double px = b.getCoordinate(0);
//            double py = b.getCoordinate(1);
//
//            b.setPosition(new double[]{
//                    px + (fs.x - px) * 0.2,
//                    py + (fs.y - py) * 0.2
//            });
//        }
//    }


}
