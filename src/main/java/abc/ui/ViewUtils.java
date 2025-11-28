package abc.ui;

import javafx.scene.canvas.GraphicsContext;

public class ViewUtils {
    // map real coordinate in [-50,50] to canvas coordinates
    public static double mapX(double x, double width, double min, double max) {
        return (x - min) / (max - min) * width;
    }

    public static double mapY(double y, double height, double min, double max) {
        // invert Y to have natural screen coordinates
        return height - ((y - min) / (max - min) * height);
    }

    public static void drawFood(GraphicsContext gc, double cx, double cy, double radius, String color, String label) {
        gc.setFill(javafx.scene.paint.Paint.valueOf(color));
        gc.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        gc.setFill(javafx.scene.paint.Paint.valueOf("BLACK"));
        if (label != null) gc.fillText(label, cx + radius + 2, cy);
    }
}
