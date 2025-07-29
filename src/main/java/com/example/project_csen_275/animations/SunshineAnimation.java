package com.example.project_csen_275.animations;

import com.example.project_csen_275.GardenLogger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Animation class to show sunshine effect on plants
 */
public class SunshineAnimation extends AnimationBase {

    // Sun image
    private ImageView sunView;

    // Sun rays
    private Line[] sunRays;

    // Glow effect
    private Circle glowCircle;

    // Fallback elements
    private Text sunText;

    /**
     * Create a new sunshine animation for the specified cell
     * 
     * @param cell The StackPane where the animation will be shown
     */
    public SunshineAnimation(StackPane cell) {
        super(cell);
        // Use a light yellow background for sunshine effect
        background.setFill(Color.rgb(255, 255, 220, 0.3));
    }

    @Override
    public void play() {
        prepareElements();
        createTimeline().play();
    }

    @Override
    protected void prepareElements() {
        try {
            Image sunImage = new Image(
                    getClass().getResourceAsStream("/com/example/project_csen_275/assests/Tiles/sun.png"));
            sunView = new ImageView(sunImage);
            sunView.setFitWidth(30);
            sunView.setFitHeight(30);
            sunView.setTranslateX(10);
            sunView.setTranslateY(-10);

            // Create a glow effect with radial gradient
            glowCircle = new Circle(20);
            glowCircle.setTranslateX(10);
            glowCircle.setTranslateY(-10);

            RadialGradient glow = new RadialGradient(
                    0, 0, 0.5, 0.5, 0.8, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 255, 0, 0.5)),
                    new Stop(1, Color.TRANSPARENT));
            glowCircle.setFill(glow);

            // Add sun rays as lines
            sunRays = new Line[8];
            for (int i = 0; i < sunRays.length; i++) {
                sunRays[i] = new Line();
                sunRays[i].setStroke(Color.GOLD);
                sunRays[i].setStrokeWidth(1.5);

                // Position rays around the sun in a circular pattern
                double angle = Math.toRadians(i * (360.0 / sunRays.length));
                double innerRadius = 15;
                double outerRadius = 25;

                sunRays[i].setStartX(10 + innerRadius * Math.cos(angle));
                sunRays[i].setStartY(-10 + innerRadius * Math.sin(angle));
                sunRays[i].setEndX(10 + outerRadius * Math.cos(angle));
                sunRays[i].setEndY(-10 + outerRadius * Math.sin(angle));

                // Add to cell
                cell.getChildren().add(sunRays[i]);
            }

            // Add elements to cell in proper order
            cell.getChildren().add(0, background);
            cell.getChildren().add(1, glowCircle);
            cell.getChildren().add(sunView);

        } catch (Exception ex) {
            // Log error and use fallback
            GardenLogger.error("Failed to load sunshine animation image: " + ex.getMessage());

            // Create sun indicator with emoji
            sunText = new Text("☀️");
            sunText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            sunText.setFill(Color.GOLD);
            sunText.setTranslateX(10);
            sunText.setTranslateY(-10);

            // Add elements to cell
            cell.getChildren().add(background);
            cell.getChildren().add(sunText);
        }
    }

    @Override
    protected Timeline createTimeline() {
        Timeline timeline = new Timeline();

        if (sunView != null && sunRays != null) {
            // Initial state
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                sunView.setOpacity(0);
                glowCircle.setOpacity(0);
                background.setOpacity(0);
                for (Line ray : sunRays) {
                    ray.setOpacity(0);
                }
                sunView.setScaleX(0.7);
                sunView.setScaleY(0.7);
            }));

            // Fade in sun
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                sunView.setOpacity(1);
                background.setOpacity(0.3);
                sunView.setScaleX(1.0);
                sunView.setScaleY(1.0);
            }));

            // Start showing rays
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), _ -> {
                for (int i = 0; i < sunRays.length; i += 2) {
                    sunRays[i].setOpacity(0.9);
                }
            }));

            // Show all rays and glow
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), _ -> {
                for (int i = 1; i < sunRays.length; i += 2) {
                    sunRays[i].setOpacity(0.9);
                }
                glowCircle.setOpacity(0.6);
            }));

            // Extend rays
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), _ -> {
                for (int i = 0; i < sunRays.length; i++) {
                    double angle = Math.toRadians(i * (360.0 / sunRays.length));
                    double outerRadius = 30;
                    sunRays[i].setEndX(10 + outerRadius * Math.cos(angle));
                    sunRays[i].setEndY(-10 + outerRadius * Math.sin(angle));
                }
                sunView.setRotate(15);
            }));

            // Rotate the sun
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), _ -> {
                sunView.setRotate(30);
                glowCircle.setOpacity(0.8);
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                sunView.setOpacity(0.5);
                glowCircle.setOpacity(0.3);
                background.setOpacity(0.1);
                for (Line ray : sunRays) {
                    ray.setOpacity(0.3);
                }
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));

        } else if (sunText != null) {
            // Fallback animation with emoji

            // Fade in
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                sunText.setOpacity(0);
                background.setOpacity(0);
                sunText.setScaleX(0.7);
                sunText.setScaleY(0.7);
            }));

            // Show sun
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                sunText.setOpacity(1);
                background.setOpacity(0.3);
            }));

            // Sun growing effect
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), _ -> {
                sunText.setScaleX(1.2);
                sunText.setScaleY(1.2);
            }));

            // Sun rotating
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), _ -> {
                sunText.setRotate(30);
                sunText.setScaleX(1.0);
                sunText.setScaleY(1.0);
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                sunText.setOpacity(0);
                background.setOpacity(0);
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));
        }

        return timeline;
    }

    @Override
    protected void cleanup() {
        if (sunView != null && sunRays != null) {
            cell.getChildren().removeAll(sunView, glowCircle, background);
            for (Line ray : sunRays) {
                cell.getChildren().remove(ray);
            }
        } else if (sunText != null) {
            cell.getChildren().removeAll(background, sunText);
        }
    }

    /**
     * Static convenience method to play sunshine animation on a cell
     * 
     * @param cell The cell to animate
     */
    public static void play(StackPane cell) {
        new SunshineAnimation(cell).play();
    }
}
