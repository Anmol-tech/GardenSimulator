package com.example.project_csen_275.animations;

import com.example.project_csen_275.GardenLogger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Animation class to show pest spray effect
 */
public class PestSprayAnimation extends AnimationBase {

    // Pest spray image
    private ImageView sprayView;

    // Spray mist elements
    private Rectangle[] mistDrops;

    // Fallback elements
    private Text sprayText;
    private Circle[] mistCircles;

    /**
     * Create a new pest spray animation for the specified cell
     * 
     * @param cell The StackPane where the animation will be shown
     */
    public PestSprayAnimation(StackPane cell) {
        super(cell);
    }

    @Override
    public void play() {
        prepareElements();
        createTimeline().play();
    }

    @Override
    protected void prepareElements() {
        try {
            // Load pest spray image
            Image sprayImage = new Image(
                    getClass().getResourceAsStream("/com/example/project_csen_275/assests/Tiles/pest-spray.png"));
            sprayView = new ImageView(sprayImage);
            sprayView.setFitWidth(35);
            sprayView.setFitHeight(35);
            sprayView.setTranslateX(10);
            sprayView.setTranslateY(-10);

            // Add a mist effect with small rectangular shapes
            mistDrops = new Rectangle[5];
            for (int i = 0; i < mistDrops.length; i++) {
                mistDrops[i] = new Rectangle(5, 5);
                mistDrops[i].setFill(Color.LIGHTGREEN);
                mistDrops[i].setOpacity(0.7);
                mistDrops[i].setArcWidth(5);
                mistDrops[i].setArcHeight(5);
                // Position mist drops at slightly different positions
                mistDrops[i].setTranslateX(25 + (i * 5) - 10);
                mistDrops[i].setTranslateY(-15 - (i % 3) * 5);

                // Add to cell
                cell.getChildren().add(mistDrops[i]);
            }

            // Add spray bottle on top of mist
            cell.getChildren().add(sprayView);

            // Add background behind all
            cell.getChildren().add(0, background);

        } catch (Exception ex) {
            // Log error but use a sophisticated fallback
            GardenLogger.error("Failed to load pest spray image: " + ex.getMessage());

            // Create spray indicator with emoji and colored circles
            sprayText = new Text("🧪");
            sprayText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            sprayText.setFill(Color.LIMEGREEN);
            sprayText.setTranslateX(5);
            sprayText.setTranslateY(-10);

            // Add green circles to represent spray mist
            mistCircles = new Circle[3];
            for (int i = 0; i < mistCircles.length; i++) {
                mistCircles[i] = new Circle(3);
                mistCircles[i].setFill(Color.LIGHTGREEN);
                mistCircles[i].setTranslateX(20 + (i * 5));
                mistCircles[i].setTranslateY(-15);

                // Add to cell
                cell.getChildren().add(mistCircles[i]);
            }

            // Add text on top of mist
            cell.getChildren().add(sprayText);

            // Add background behind all
            cell.getChildren().add(0, background);
        }
    }

    @Override
    protected Timeline createTimeline() {
        Timeline timeline = new Timeline();

        if (sprayView != null && mistDrops != null) {
            // Initial state
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                sprayView.setOpacity(0);
                background.setOpacity(0);
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
                background.setOpacity(0.2);
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
                background.setOpacity(0.1);
                for (Rectangle mist : mistDrops) {
                    mist.setOpacity(0.3);
                }
                sprayView.setRotate(0);
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));

        } else if (sprayText != null && mistCircles != null) {
            // Fallback animation with emoji and circles

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

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));
        }

        return timeline;
    }

    @Override
    protected void cleanup() {
        if (sprayView != null && mistDrops != null) {
            cell.getChildren().remove(sprayView);
            for (Rectangle mist : mistDrops) {
                cell.getChildren().remove(mist);
            }
            cell.getChildren().remove(background);
        } else if (sprayText != null && mistCircles != null) {
            cell.getChildren().remove(sprayText);
            for (Circle mist : mistCircles) {
                cell.getChildren().remove(mist);
            }
            cell.getChildren().remove(background);
        }
    }

    /**
     * Static convenience method to play pest spray animation on a cell
     * 
     * @param cell The cell to animate
     */
    public static void play(StackPane cell) {
        new PestSprayAnimation(cell).play();
    }
}
