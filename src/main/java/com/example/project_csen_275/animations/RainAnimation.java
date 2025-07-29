package com.example.project_csen_275.animations;

import com.example.project_csen_275.GardenLogger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Animation class to show rain effect on plants
 */
public class RainAnimation extends AnimationBase {

    // Rain cloud image
    private ImageView cloudView;

    // Rain drops
    private Line[] rainDrops;

    // Fallback elements
    private Text rainText;

    /**
     * Create a new rain animation for the specified cell
     * 
     * @param cell The StackPane where the animation will be shown
     */
    public RainAnimation(StackPane cell) {
        super(cell);
        // Use a light blue background for rain effect
        background.setFill(Color.rgb(230, 240, 250));
    }

    @Override
    public void play() {
        prepareElements();
        createTimeline().play();
    }

    @Override
    protected void prepareElements() {
        try {
            // Using a suitable tile for cloud image
            Image cloudImage = new Image(getClass()
                    .getResourceAsStream("/com/example/project_csen_275/assests/Tiles/rain.png"));
            cloudView = new ImageView(cloudImage);
            cloudView.setFitWidth(35);
            cloudView.setFitHeight(25);
            cloudView.setTranslateX(10);
            cloudView.setTranslateY(-20);

            // Add rain drops as vertical lines
            rainDrops = new Line[6];
            for (int i = 0; i < rainDrops.length; i++) {
                rainDrops[i] = new Line();
                rainDrops[i].setStroke(Color.DEEPSKYBLUE);
                rainDrops[i].setStrokeWidth(1.5);

                // Position rain drops below the cloud
                double xPos = 5 + (i * 5);
                rainDrops[i].setStartX(xPos);
                rainDrops[i].setStartY(-10);
                rainDrops[i].setEndX(xPos);
                rainDrops[i].setEndY(-5);

                // Add to cell
                cell.getChildren().add(rainDrops[i]);
            }

            // Add elements to cell
            cell.getChildren().add(0, background);
            cell.getChildren().add(cloudView);

        } catch (Exception ex) {
            // Log error and use fallback
            GardenLogger.error("Failed to load rain animation image: " + ex.getMessage());

            // Create rain indicator with emoji
            rainText = new Text("🌧️");
            rainText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            rainText.setFill(Color.DARKBLUE);
            rainText.setTranslateX(10);
            rainText.setTranslateY(-10);

            // Add elements to cell
            cell.getChildren().add(background);
            cell.getChildren().add(rainText);
        }
    }

    @Override
    protected Timeline createTimeline() {
        Timeline timeline = new Timeline();

        if (cloudView != null && rainDrops != null) {
            // Initial state
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                cloudView.setOpacity(0);
                background.setOpacity(0);
                for (Line drop : rainDrops) {
                    drop.setOpacity(0);
                }
            }));

            // Fade in cloud
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                cloudView.setOpacity(1);
                background.setOpacity(0.3);
            }));

            // Start rain - initial drops
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), _ -> {
                for (int i = 0; i < rainDrops.length; i += 2) {
                    rainDrops[i].setOpacity(0.9);
                }
            }));

            // Continue rain - more drops
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), _ -> {
                for (int i = 1; i < rainDrops.length; i += 2) {
                    rainDrops[i].setOpacity(0.9);
                }
            }));

            // Rain falling animation
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), _ -> {
                for (int i = 0; i < rainDrops.length; i++) {
                    // Extend the rain drops downward
                    rainDrops[i].setEndY(0);
                }
            }));

            // More rain falling
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), _ -> {
                for (int i = 0; i < rainDrops.length; i++) {
                    // Extend the rain drops further
                    rainDrops[i].setEndY(5);
                    // Slightly move start point down
                    rainDrops[i].setStartY(-5);
                }
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                cloudView.setOpacity(0.5);
                background.setOpacity(0.1);
                for (Line drop : rainDrops) {
                    drop.setOpacity(0.3);
                }
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));

        } else if (rainText != null) {
            // Fallback animation with emoji

            // Fade in
            timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO, _ -> {
                rainText.setOpacity(0);
                background.setOpacity(0);
            }));

            // Show rain cloud
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), _ -> {
                rainText.setOpacity(1);
                background.setOpacity(0.3);
            }));

            // Rain effect with scaling
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), _ -> {
                rainText.setScaleX(1.1);
                rainText.setScaleY(1.1);
            }));

            // Return to normal
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), _ -> {
                rainText.setScaleX(1.0);
                rainText.setScaleY(1.0);
            }));

            // Begin fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                rainText.setOpacity(0);
                background.setOpacity(0);
            }));

            // Complete fade out
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));
        }

        return timeline;
    }

    @Override
    protected void cleanup() {
        if (cloudView != null && rainDrops != null) {
            cell.getChildren().remove(cloudView);
            for (Line drop : rainDrops) {
                cell.getChildren().remove(drop);
            }
            cell.getChildren().remove(background);
        } else if (rainText != null) {
            cell.getChildren().removeAll(background, rainText);
        }
    }

    /**
     * Static convenience method to play rain animation on a cell
     * 
     * @param cell The cell to animate
     */
    public static void play(StackPane cell) {
        new RainAnimation(cell).play();
    }
}
