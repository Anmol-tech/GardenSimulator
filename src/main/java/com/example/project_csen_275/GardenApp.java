package com.example.project_csen_275;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class GardenApp extends Application {
    private GardenControllerFX controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("garden-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1024, 768); // Further increased window size for better visibility
        stage.setResizable(true);
        stage.setScene(scene);
        stage.setTitle("Garden Simulator");
        stage.centerOnScreen(); // Center window on screen

        // Get controller reference for cleanup
        controller = loader.getController();

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Clean up resources when application is closing
        if (controller != null) {
            controller.cleanup();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
