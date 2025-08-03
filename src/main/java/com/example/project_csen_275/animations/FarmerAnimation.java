package com.example.project_csen_275.animations;

import com.example.project_csen_275.GardenLogger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Animation class to show farmer/gardener taking action
 */
public class FarmerAnimation extends AnimationBase {

    // Farmer image
    private ImageView farmerView;

    // Soil particles
    private Circle[] soilParticles;

    // Fallback elements
    private Text farmerText;

    /**
     * Create a new farmer animation for the specified cell
     * 
     * @param cell The StackPane where the animation will be shown
     */
    public FarmerAnimation(StackPane cell) {
        super(cell);
        // Use a light brown background for farming/soil effect
        background.setFill(Color.rgb(210, 180, 140, 0.3));
    }

    @Override
    public void play() {
        prepareElements();
        createTimeline().play();
    }

    @Override
    protected void prepareElements() {
        try {
            // Load farmer image
            Image farmerImage = new Image(getClass()
                    .getResourceAsStream("/com/example/project_csen_275/assests/Tiles/gardener.png")); // Using farmer
                                                                                                       // tile
            farmerView = new ImageView(farmerImage);
            farmerView.setFitWidth(30);
            farmerView.setFitHeight(30);
            farmerView.setTranslateX(10);
            farmerView.setTranslateY(-10);

            // Add soil particles
            soilParticles = new Circle[5];
            for (int i = 0; i < soilParticles.length; i++) {
                soilParticles[i] = new Circle(2);
                soilParticles[i].setFill(Color.rgb(139, 69, 19)); // Brown color

                // Position particles at the bottom part of the cell
                soilParticles[i].setTranslateX(5 + (i * 5));
                soilParticles[i].setTranslateY(0);

                // Add to cell
                cell.getChildren().add(soilParticles[i]);
            }

            // Add elements to cell
            cell.getChildren().addFirst(background);
            cell.getChildren().add(farmerView);

        } catch (Exception ex) {
            // Log error and use fallback
            GardenLogger.error("Failed to load farmer animation image: " + ex.getMessage());

            // Create farmer indicator with emoji
            farmerText = new Text("👨‍🌾");
            farmerText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            farmerText.setFill(Color.BROWN);
            farmerText.setTranslateX(10);
            farmerText.setTranslateY(-10);

            // Add elements to cell
            cell.getChildren().add(background);
            cell.getChildren().add(farmerText);
        }
    }

    @Override
    protected Timeline createTimeline() {
        Timeline timeline = new Timeline();

        if (farmerView != null && soilParticles != null) {
            // Initial state
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                farmerView.setOpacity(0);
                background.setOpacity(0);
                for (Circle particle : soilParticles) {
                    particle.setOpacity(0);
                }
            }));

            // Fade in farmer
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                farmerView.setOpacity(1);
                background.setOpacity(0.3);
                farmerView.setTranslateY(-15); // Start slightly higher
            }));

            // Farmer working animation - move down
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250), _ -> {
                farmerView.setTranslateY(-5); // Move down as if digging
                farmerView.setScaleX(1.1);
                farmerView.setScaleY(1.1);

                // Show first soil particles
                for (int i = 0; i < 2; i++) {
                    soilParticles[i].setOpacity(0.8);
                    soilParticles[i].setTranslateY(-2); // Move up slightly as if being dug
                }
            }));

            // Continue working animation - move up
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), _ -> {
                farmerView.setTranslateY(-10); // Move back up
                farmerView.setScaleX(1.0);
                farmerView.setScaleY(1.0);

                // Show more soil particles
                for (int i = 0; i < soilParticles.length; i++) {
                    soilParticles[i].setOpacity(0.8);
                    soilParticles[i].setTranslateY(-5 + (i % 3)); // Different heights
                }
            }));

            // Soil settling animation
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(550), _ -> {
                // Soil particles fall back
                for (Circle soilParticle : soilParticles) {
                    soilParticle.setTranslateY(0);
                }

                // Farmer moves side to side slightly
                farmerView.setTranslateX(13);
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                farmerView.setOpacity(0.5);
                background.setOpacity(0.1);
                for (Circle particle : soilParticles) {
                    particle.setOpacity(0.3);
                }
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));

        } else if (farmerText != null) {
            // Fallback animation with emoji

            // Fade in
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                farmerText.setOpacity(0);
                background.setOpacity(0);
            }));

            // Show farmer
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                farmerText.setOpacity(1);
                background.setOpacity(0.3);
            }));

            // Farmer working animation
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250), _ -> {
                farmerText.setTranslateY(-5);
                farmerText.setScaleX(1.1);
                farmerText.setScaleY(1.1);
            }));

            // Continue animation
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), _ -> {
                farmerText.setTranslateY(-10);
                farmerText.setScaleX(1.0);
                farmerText.setScaleY(1.0);
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                farmerText.setOpacity(0);
                background.setOpacity(0);
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));
        }

        return timeline;
    }

    @Override
    protected void cleanup() {
        if (farmerView != null && soilParticles != null) {
            cell.getChildren().remove(farmerView);
            for (Circle particle : soilParticles) {
                cell.getChildren().remove(particle);
            }
            cell.getChildren().remove(background);
        } else if (farmerText != null) {
            cell.getChildren().removeAll(background, farmerText);
        }
    }

    /**
     * Static convenience method to play farmer animation on a cell
     * 
     * @param cell The cell to animate
     */
    public static void play(StackPane cell) {
        new FarmerAnimation(cell).play();
    }
}
