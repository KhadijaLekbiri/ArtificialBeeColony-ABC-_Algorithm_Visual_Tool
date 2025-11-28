package abc.ui;

import abc.algorithm.ABCAlgorithm;
import abc.algorithm.FoodSource;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.util.List;

public class Controller {

    @FXML public Canvas canvas;
    @FXML public Button startBtn;
    @FXML public Button pauseBtn;
    @FXML public Button nextBtn;
    @FXML public Button resetBtn;
    @FXML public Slider foodSlider;
    @FXML public Label foodCountLabel;
    @FXML public Label iterationLabel;

    private ABCAlgorithm algorithm;
    private AnimationTimer timer;
    private boolean running = false;

    // visualization bounds (should match algorithm)
    private final double min = -50;
    private final double max = 50;

    @FXML
    public void initialize() {
        algorithm = new ABCAlgorithm();
        algorithm.setUpdateListener(this::onAlgorithmUpdate);
        canvas.setWidth(800);
        canvas.setHeight(600);
        foodCountLabel.setText(String.valueOf((int) foodSlider.getValue()));
        foodSlider.valueProperty().addListener((obs, oldV, newV) -> {
            foodCountLabel.setText(String.valueOf(newV.intValue()));
        });

        timer = new AnimationTimer() {
            private long prev = 0;
            private final long INTERVAL = 100_000_000; // 100ms
            @Override
            public void handle(long now) {
                if (prev == 0) prev = now;
                if (now - prev >= INTERVAL) {
                    algorithm.runIteration();
                    prev = now;
                }
            }
        };

        // initial render
        algorithm.init();
        render();
    }

    // UI callbacks
    @FXML
    public void onStart() {
        if (!running) {
            timer.start();
            running = true;
        }
    }

    @FXML
    public void onPause() {
        if (running) {
            timer.stop();
            running = false;
        }
    }

    @FXML
    public void onNext() {
        algorithm.runIteration();
    }

    @FXML
    public void onReset() {
        algorithm.init();
        render();
    }

    @FXML
    public void onFoodSliderChange() {
        int n = (int) foodSlider.getValue();
        algorithm.setFoodNumber(n);
        render();
    }

    private void onAlgorithmUpdate(List<FoodSource> foods, int iteration) {
        iterationLabel.setText("Iteration: " + iteration);
        render();
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        List<FoodSource> foods = algorithm.getFoods();
        if (foods == null) return;
        // draw each source, size proportional to fitness (or inverse of objective)
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        for (int i = 0; i < foods.size(); i++) {
            FoodSource f = foods.get(i);
            double x = f.getPosition()[0];
            double y = f.getPosition()[1];
            double cx = ViewUtils.mapX(x, w, min, max);
            double cy = ViewUtils.mapY(y, h, min, max);
            double size = Math.max(3, 10 * (1.0 / (1 + f.getObjective())) * 5);
            String color = "gold"; // all sources gold; you can vary by trial or fitness
            ViewUtils.drawFood(gc, cx, cy, size, color, String.valueOf(i));
        }
        // highlight best
        FoodSource best = algorithm.getBest();
        if (best != null) {
            double bx = ViewUtils.mapX(best.getPosition()[0], w, min, max);
            double by = ViewUtils.mapY(best.getPosition()[1], h, min, max);
            gc.setStroke(javafx.scene.paint.Paint.valueOf("RED"));
            gc.strokeOval(bx - 12, by - 12, 24, 24);
            gc.fillText(String.format("Best: %.5f", best.getObjective()), 10, 20);
        }
    }
}
