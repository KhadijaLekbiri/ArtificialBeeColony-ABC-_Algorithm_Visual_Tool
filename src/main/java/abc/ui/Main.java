// src/main/java/abc/ui/Main.java
package abc.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Artificial Bee Colony (ABC) Algorithm Visualizer");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        // ✅ Do NOT call controller methods here — handled in initialize()
    }

    public static void main(String[] args) {
        launch(args);
    }
}