package com.example.project_csen_275;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
        KeyFrame fadeIn = new KeyFrame(Duration.ZERO, _ -> {
            wateringCanText.setOpacity(0);
            background.setOpacity(0);
        });
        timeline.getKeyFrames().add(fadeIn);

        // Add visible keyframe
        KeyFrame visible = new KeyFrame(Duration.millis(100), _ -> {
            wateringCanText.setOpacity(1);
            background.setOpacity(0.5);
        });
        timeline.getKeyFrames().add(visible);

        // Add fade-out keyframe
        KeyFrame fadeOut = new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
            wateringCanText.setOpacity(0);
            background.setOpacity(0);
        });
        timeline.getKeyFrames().add(fadeOut);

        // Add removal keyframe
        KeyFrame removal = new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> {
            cell.getChildren().removeAll(background, wateringCanText);
        });
        timeline.getKeyFrames().add(removal);

        // Play the timeline
        timeline.play();
    }

    /**
     * Show pest spray animation with spray image on the specified cell.
     * Includes a spraying effect with rotation and scaling for more visual impact.
     */
    public static void playPestSprayAnimation(StackPane cell) {
        try {
            // Load pest spray image
            Image sprayImage = new Image(WaterAnimation.class.getResourceAsStream("assests/Tiles/pest-spray.png"));
            ImageView sprayView = new ImageView(sprayImage);
            sprayView.setFitWidth(35); // Slightly smaller for better appearance
            sprayView.setFitHeight(35);
            sprayView.setTranslateX(10);
            sprayView.setTranslateY(-10);

            // Add a mist effect with small circular shapes
            Rectangle[] mistDrops = new Rectangle[5];
            for (int i = 0; i < mistDrops.length; i++) {
                mistDrops[i] = new Rectangle(5, 5);
                mistDrops[i].setFill(Color.LIGHTGREEN);
                mistDrops[i].setOpacity(0.7);
                mistDrops[i].setArcWidth(5);
                mistDrops[i].setArcHeight(5);
                // Position mist drops at slightly different positions
                mistDrops[i].setTranslateX(25 + (i * 5) - 10);
                mistDrops[i].setTranslateY(-15 - (i % 3) * 5);
            }

            for (Rectangle mist : mistDrops) {
                cell.getChildren().add(mist);
            }
            cell.getChildren().add(sprayView);

            // Create a more interesting animation with rotation and scaling
            Timeline timeline = new Timeline();

            // Initial state
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                sprayView.setOpacity(0);

                sprayView.setRotate(0);
                sprayView.setScaleX(0.8);
                sprayView.setScaleY(0.8);
                for (Rectangle mist : mistDrops) {
                    mist.setOpacity(0);
                }
            }));

            // Fade in spray bottle
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                sprayView.setOpacity(1);

                sprayView.setRotate(-15); // Slight tilt as if spraying
            }));

            // Show mist effect
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), _ -> {
                for (int i = 0; i < mistDrops.length; i++) {
                    mistDrops[i].setOpacity(0.7);
                }
                sprayView.setRotate(-30); // More tilt for spraying action
                sprayView.setScaleX(1.1);
                sprayView.setScaleY(1.1);
            }));

            // Disperse mist
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), _ -> {
                for (int i = 0; i < mistDrops.length; i++) {
                    mistDrops[i].setTranslateX(mistDrops[i].getTranslateX() + (i * 2));
                    mistDrops[i].setTranslateY(mistDrops[i].getTranslateY() - (i % 2) * 3);
                }
                sprayView.setRotate(-15); // Return to less tilt
                sprayView.setScaleX(1.0);
                sprayView.setScaleY(1.0);
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                sprayView.setOpacity(0.5);

                for (Rectangle mist : mistDrops) {
                    mist.setOpacity(0.3);
                }
                sprayView.setRotate(0);
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> {
                cell.getChildren().removeAll(sprayView);
                for (Rectangle mist : mistDrops) {
                    cell.getChildren().remove(mist);
                }
            }));

            timeline.play();
        } catch (Exception ex) {
            // Log error but use a more sophisticated fallback
            System.err.println("Could not load pest-spray.png image: " + ex.getMessage());
            GardenLogger.error("Failed to load pest spray image: " + ex.getMessage());

            // Create spray indicator with both emoji and colored shapes for visual effect
            Text sprayText = new Text("🧪"); // Spray bottle emoji
            sprayText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            sprayText.setFill(Color.LIMEGREEN);
            sprayText.setTranslateX(5);
            sprayText.setTranslateY(-10);

            // Add green circles to represent spray mist
            Circle[] mistCircles = new Circle[3];
            for (int i = 0; i < mistCircles.length; i++) {
                mistCircles[i] = new Circle(3);
                mistCircles[i].setFill(Color.LIGHTGREEN);
                mistCircles[i].setTranslateX(20 + (i * 5));
                mistCircles[i].setTranslateY(-15);
            }

            Rectangle background = new Rectangle(40, 40);
            background.setFill(Color.WHITE);
            background.setOpacity(0.3);
            background.setArcWidth(10);
            background.setArcHeight(10);
            background.setTranslateX(10);
            background.setTranslateY(-10);

            // Add elements to cell
            cell.getChildren().add(background);
            for (Circle mist : mistCircles) {
                cell.getChildren().add(mist);
            }
            cell.getChildren().add(sprayText);

            // Animation timeline
            Timeline timeline = new Timeline();

            // Fade in
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                sprayText.setOpacity(0);
                background.setOpacity(0);
                for (Circle mist : mistCircles) {
                    mist.setOpacity(0);
                }
            }));

            // Show spray
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                sprayText.setOpacity(1);
                background.setOpacity(0.3);
            }));

            // Show mist
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), _ -> {
                for (int i = 0; i < mistCircles.length; i++) {
                    mistCircles[i].setOpacity(0.8);
                }
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                sprayText.setOpacity(0);
                background.setOpacity(0);
                for (Circle mist : mistCircles) {
                    mist.setOpacity(0);
                }
            }));

            // Remove elements
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> {
                cell.getChildren().removeAll(background, sprayText);
                for (Circle mist : mistCircles) {
                    cell.getChildren().remove(mist);
                }
            }));

            timeline.play();
        }
    }
}
