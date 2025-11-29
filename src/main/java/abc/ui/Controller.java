// src/main/java/abc/ui/Controller.java
package abc.ui;

import abc.algorithm.ABCAlgorithm;
import abc.algorithm.FoodSource;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {

    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 600;
    private static final double PADDING = 50;

    @FXML private Canvas canvas;
    @FXML private Label statusLabel;
    @FXML private Button startButton, pauseButton, resetButton;
    @FXML private Slider speedSlider;
    @FXML private CheckBox showNeighborhood;
    @FXML private TextField foodCountField, limitField, radiusField;
    @FXML private Label iterationText;
    @FXML private Label bestFitnessLabel;
    @FXML private Label avgFitnessLabel;
    @FXML private Label abandonedLabel;
    @FXML private Label phaseLabel;

    private GraphicsContext gc;
    private ABCAlgorithm abc;
    private AnimationTimer timer;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public void initialize() {
        canvas.setWidth(CANVAS_WIDTH);
        canvas.setHeight(CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Add educational tooltips
        setupTooltips();

        // Initialize algorithm and draw
        initializeAlgorithm();
    }

    private void setupTooltips() {
        Tooltip canvasTooltip = new Tooltip(
                "VISUALIZATION GUIDE:\n\n" +
                        "🎯 Green Circles = High nectar (good solutions)\n" +
                        "🎯 Red Circles = Low nectar (poor solutions)\n" +
                        "🔴 Red Numbers = Trial count (near abandonment)\n" +
                        "🔵 Blue Circles = Search neighborhood\n" +
                        "⭐ Size = Fitness level (bigger = better)\n\n" +
                        "ALGORITHM PHASES:\n" +
                        "1. Employed Bees: Local search around known food sources\n" +
                        "2. Onlooker Bees: Fitness-proportional selection & search\n" +
                        "3. Scout Bees: Abandon poor sources & discover new ones"
        );
        canvasTooltip.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        Tooltip.install(canvas, canvasTooltip);

        Tooltip.install(showNeighborhood, new Tooltip(
                "Shows the search area around each bee where new solutions are explored"
        ));

        Tooltip.install(foodCountField, new Tooltip("Number of employed bees (food sources)"));
        Tooltip.install(limitField, new Tooltip("Maximum trials before a food source is abandoned"));
        Tooltip.install(radiusField, new Tooltip("Search neighborhood radius around each food source"));
    }

    public void initializeAlgorithm() {
        try {
            int foodCount = Integer.parseInt(foodCountField.getText());
            int limit = Integer.parseInt(limitField.getText());
            double radius = Double.parseDouble(radiusField.getText());
            abc = new ABCAlgorithm(foodCount, limit, radius);
            draw();
            updateStats();
            statusLabel.setText("Ready");
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid input! Please enter valid numbers.").show();
        }
    }

    @FXML
    private void startSimulation() {
        if (running.get()) return;
        running.set(true);
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        statusLabel.setText("Running...");

        final long[] lastTime = {0};
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!running.get()) return;
                double speed = speedSlider.getValue();
                if (now - lastTime[0] < (1_000_000_000L / speed)) return;
                lastTime[0] = now;

                abc.runIteration();
                draw();
                updateStats();

                if (abc.getCurrentIteration() >= abc.getMaxIterations() || abc.isConverged()) {
                    running.set(false);
                    statusLabel.setText("Completed! " +
                            (abc.isConverged() ? "Solution converged!" : "Max iterations reached"));
                    startButton.setDisable(false);
                    pauseButton.setDisable(true);
                    stop();
                }
            }
        };
        timer.start();
    }

    @FXML
    private void pauseSimulation() {
        running.set(false);
        if (timer != null) timer.stop();
        statusLabel.setText("Paused");
        startButton.setDisable(false);
        pauseButton.setDisable(true);
    }

    @FXML
    private void resetAlgorithm() {
        if (timer != null) timer.stop();
        running.set(false);
        initializeAlgorithm();
        iterationText.setText("Iteration: 0");
        phaseLabel.setText("Phase: Initialization");
    }

    private void draw() {
        if (abc == null) return;

        // Clear canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Draw function landscape background
        drawFunctionBackground();

        // Draw boundary
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeRect(PADDING, PADDING, CANVAS_WIDTH - 2*PADDING, CANVAS_HEIGHT - 2*PADDING);

        // Draw neighborhood circles
        if (showNeighborhood.isSelected()) {
            drawNeighborhoods();
        }

        // Draw food sources
        drawFoodSources();

        // Draw algorithm information
        drawAlgorithmInfo();

        // Draw phase indicator
        drawPhaseIndicator();
    }

    private void drawFunctionBackground() {
        // Draw Rastrigin function contour background
        int steps = 30;
        double stepX = (CANVAS_WIDTH - 2*PADDING) / steps;
        double stepY = (CANVAS_HEIGHT - 2*PADDING) / steps;

        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                double x = PADDING + i * stepX;
                double y = PADDING + j * stepY;
                double normX = (x - PADDING) / (CANVAS_WIDTH - 2*PADDING);
                double normY = (y - PADDING) / (CANVAS_HEIGHT - 2*PADDING);

                double fitness = abc.calculateFitness(normX, normY);
                double intensity = fitness / 100.0;

                // Create gradient from dark blue (low fitness) to light blue (high fitness)
                Color color = Color.color(0, 0.3, 0.8, 0.3 + intensity * 0.3);
                gc.setFill(color);
                gc.fillRect(x, y, stepX, stepY);
            }
        }
    }

    private void drawNeighborhoods() {
        gc.setStroke(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.3));
        gc.setLineWidth(1);
        for (FoodSource fs : abc.getFoodSources()) {
            double sx = PADDING + fs.getX() * (CANVAS_WIDTH - 2*PADDING);
            double sy = PADDING + fs.getY() * (CANVAS_HEIGHT - 2*PADDING);
            double r = abc.getNeighborhoodRadius() * (CANVAS_WIDTH - 2*PADDING);
            gc.strokeOval(sx - r, sy - r, 2*r, 2*r);
        }
    }

    private void drawFoodSources() {
        for (FoodSource fs : abc.getFoodSources()) {
            double sx = PADDING + fs.getX() * (CANVAS_WIDTH - 2*PADDING);
            double sy = PADDING + fs.getY() * (CANVAS_HEIGHT - 2*PADDING);
            double fitRatio = fs.getFitnessRatio();

            // Color from green (high fitness) to red (low fitness)
            Color color = Color.hsb(120 * fitRatio, 1.0, 1.0);
            double size = 6 + fs.getFitness() / 15.0; // Size indicates fitness

            // Draw food source circle
            gc.setFill(color);
            gc.fillOval(sx - size, sy - size, size*2, size*2);

            // Draw outline
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeOval(sx - size, sy - size, size*2, size*2);

            // Draw trial count if near abandonment
            if (fs.getTrialCount() > abc.getLimit() * 0.7) {
                gc.setFill(Color.RED);
                gc.setFont(Font.font(10));
                gc.fillText(String.valueOf(fs.getTrialCount()), sx - 5, sy - size - 5);
            }
        }
    }

    private void drawAlgorithmInfo() {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));

        // Legend
        gc.fillText("LEGEND: 🟢 High Fitness → 🔴 Low Fitness | 🔴 Numbers = Trial Count", 10, 25);

        // Best solution indicator
        if (!abc.getFoodSources().isEmpty()) {
            FoodSource best = abc.getFoodSources().stream()
                    .max((a, b) -> Double.compare(a.getFitness(), b.getFitness()))
                    .orElse(abc.getFoodSources().get(0));

            double sx = PADDING + best.getX() * (CANVAS_WIDTH - 2*PADDING);
            double sy = PADDING + best.getY() * (CANVAS_HEIGHT - 2*PADDING);

            // Draw star around best solution
            gc.setStroke(Color.GOLD);
            gc.setLineWidth(2);
            gc.strokeOval(sx - 12, sy - 12, 24, 24);
            gc.fillText("⭐ Best Solution", sx + 15, sy - 15);
        }
    }

    private void drawPhaseIndicator() {
        gc.setFill(Color.DARKBLUE);
        gc.setFont(Font.font(14));
        gc.fillText("Current Phase: " + abc.getCurrentPhase(), 10, CANVAS_HEIGHT - 20);
    }

    private void updateStats() {
        if (abc == null) return;
        bestFitnessLabel.setText(String.format("%.1f", abc.getBestFitness()));
        avgFitnessLabel.setText(String.format("%.1f", abc.getAverageFitness()));
        abandonedLabel.setText(String.valueOf(abc.getAbandonedCount()));
        iterationText.setText("Iteration: " + abc.getCurrentIteration() + "/" + abc.getMaxIterations());
        phaseLabel.setText("Phase: " + abc.getCurrentPhase());

        // Update phase label color based on current phase
        String phase = abc.getCurrentPhase().toLowerCase();
        if (phase.contains("employed")) {
            phaseLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else if (phase.contains("onlooker")) {
            phaseLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        } else if (phase.contains("scout")) {
            phaseLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            phaseLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        }
    }
}