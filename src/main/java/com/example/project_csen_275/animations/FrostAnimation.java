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
 * Animation class to show frost/cold effect on plants
 */
public class FrostAnimation extends AnimationBase {

    // Frost/snowflake image
    private ImageView snowflakeView;

    // Frost particles
    private Circle[] frostParticles;

    // Fallback elements
    private Text snowflakeText;

    /**
     * Create a new frost animation for the specified cell
     * 
     * @param cell The StackPane where the animation will be shown
     */
    public FrostAnimation(StackPane cell) {
        super(cell);
        // Use a light blue background for frost effect
        background.setFill(Color.rgb(240, 248, 255));
    }

    @Override
    public void play() {
        prepareElements();
        createTimeline().play();
    }

    @Override
    protected void prepareElements() {
        try {
            // Use a tile from the existing assets as a snowflake/frost icon
            Image snowflakeImage = new Image(getClass()
                    .getResourceAsStream("/com/example/project_csen_275/assests/Tiles/frost.png")); // Using an
                                                                                                    // appropriate tile
                                                                                                    // as snowflake
            snowflakeView = new ImageView(snowflakeImage);
            snowflakeView.setFitWidth(30);
            snowflakeView.setFitHeight(30);
            snowflakeView.setTranslateX(10);
            snowflakeView.setTranslateY(-10);

            // Add frost particles
            frostParticles = new Circle[6];
            for (int i = 0; i < frostParticles.length; i++) {
                frostParticles[i] = new Circle(2);
                frostParticles[i].setFill(Color.LIGHTBLUE);
                // Position frost particles in a circular pattern around the snowflake
                double angle = i * (Math.PI * 2 / frostParticles.length);
                frostParticles[i].setTranslateX(10 + Math.cos(angle) * 15);
                frostParticles[i].setTranslateY(-10 + Math.sin(angle) * 15);

                // Add to cell
                cell.getChildren().add(frostParticles[i]);
            }

            // Add elements to cell
            cell.getChildren().add(background);
            cell.getChildren().add(snowflakeView);

        } catch (Exception ex) {
            // Log error and use fallback
            GardenLogger.error("Failed to load frost animation image: " + ex.getMessage());

            // Create frost indicator with emoji
            snowflakeText = new Text("❄️");
            snowflakeText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            snowflakeText.setFill(Color.LIGHTBLUE);
            snowflakeText.setTranslateX(10);
            snowflakeText.setTranslateY(-10);

            // Add elements to cell
            cell.getChildren().add(background);
            cell.getChildren().add(snowflakeText);
        }
    }

    @Override
    protected Timeline createTimeline() {
        Timeline timeline = new Timeline();

        if (snowflakeView != null && frostParticles != null) {
            // Initial state
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                snowflakeView.setOpacity(0);
                background.setOpacity(0);
                snowflakeView.setRotate(0);
                for (Circle particle : frostParticles) {
                    particle.setOpacity(0);
                    particle.setRadius(1);
                }
            }));

            // Fade in snowflake
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                snowflakeView.setOpacity(1);
                background.setOpacity(0.3);
            }));

            // Show frost particles and rotate snowflake
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), _ -> {
                snowflakeView.setRotate(15);
                for (Circle particle : frostParticles) {
                    particle.setOpacity(0.8);
                }
            }));

            // Expand frost effect
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), _ -> {
                snowflakeView.setRotate(30);
                for (int i = 0; i < frostParticles.length; i++) {
                    // Move particles outward
                    double angle = i * (Math.PI * 2 / frostParticles.length);
                    double x = 10 + Math.cos(angle) * 20;
                    double y = -10 + Math.sin(angle) * 20;
                    frostParticles[i].setTranslateX(x);
                    frostParticles[i].setTranslateY(y);
                    frostParticles[i].setRadius(3);
                }
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                snowflakeView.setOpacity(0.5);
                background.setOpacity(0.1);
                for (Circle particle : frostParticles) {
                    particle.setOpacity(0.3);
                }
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));

        } else if (snowflakeText != null) {
            // Fallback animation with emoji

            // Fade in
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                snowflakeText.setOpacity(0);
                background.setOpacity(0);
                snowflakeText.setRotate(0);
            }));

            // Show snowflake
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                snowflakeText.setOpacity(1);
                background.setOpacity(0.3);
            }));

            // Rotate snowflake
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250), _ -> {
                snowflakeText.setRotate(30);
            }));

            // Rotate more
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), _ -> {
                snowflakeText.setRotate(60);
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                snowflakeText.setOpacity(0);
                background.setOpacity(0);
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));
        }

        return timeline;
    }

    @Override
    protected void cleanup() {
        if (snowflakeView != null && frostParticles != null) {
            cell.getChildren().remove(snowflakeView);
            for (Circle particle : frostParticles) {
                cell.getChildren().remove(particle);
            }
            cell.getChildren().remove(background);
        } else if (snowflakeText != null) {
            cell.getChildren().removeAll(background, snowflakeText);
        }
    }

    /**
     * Static convenience method to play frost animation on a cell
     * 
     * @param cell The cell to animate
     */
    public static void play(StackPane cell) {
        new FrostAnimation(cell).play();
    }
}
