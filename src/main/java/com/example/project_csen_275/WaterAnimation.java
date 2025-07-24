package com.example.project_csen_275;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * WaterAnimation class to show watering can icon when plants are watered
 */
public class WaterAnimation {

    // Animation duration in milliseconds
    private static final int DISPLAY_DURATION = 800;

    /**
     * Show watering can icon on the specified cell
     * 
     * @param cell The StackPane where the watering can will be shown
     */
    public static void playWaterAnimation(StackPane cell) {
        // Create watering can indicator with multiple water drop emojis
        Text wateringCanText = new Text("💧💦");
        wateringCanText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        wateringCanText.setFill(Color.DEEPSKYBLUE);
        wateringCanText.setTranslateX(10); // Position slightly to the right
        wateringCanText.setTranslateY(-10); // Position slightly above center

        // Create background for better visibility
        Rectangle background = new Rectangle(30, 30);
        background.setFill(Color.WHITE);
        background.setOpacity(0.5);
        background.setArcWidth(10);
        background.setArcHeight(10);
        background.setTranslateX(10);
        background.setTranslateY(-10);

        // Add to cell
        cell.getChildren().addAll(background, wateringCanText);

        // Create a more interesting animation with fade-in and fade-out
        Timeline timeline = new Timeline();

        // Add fade-in keyframe
        KeyFrame fadeIn = new KeyFrame(Duration.ZERO, e -> {
            wateringCanText.setOpacity(0);
            background.setOpacity(0);
        });
        timeline.getKeyFrames().add(fadeIn);

        // Add visible keyframe
        KeyFrame visible = new KeyFrame(Duration.millis(100), e -> {
            wateringCanText.setOpacity(1);
            background.setOpacity(0.5);
        });
        timeline.getKeyFrames().add(visible);

        // Add fade-out keyframe
        KeyFrame fadeOut = new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), e -> {
            wateringCanText.setOpacity(0);
            background.setOpacity(0);
        });
        timeline.getKeyFrames().add(fadeOut);

        // Add removal keyframe
        KeyFrame removal = new KeyFrame(Duration.millis(DISPLAY_DURATION), e -> {
            cell.getChildren().removeAll(background, wateringCanText);
        });
        timeline.getKeyFrames().add(removal);

        // Play the timeline
        timeline.play();
    }
}
