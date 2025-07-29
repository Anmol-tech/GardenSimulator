package com.example.project_csen_275.animations;

import com.example.project_csen_275.GardenLogger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Animation class to show watering can icon when plants are watered
 */
public class WaterAnimation extends AnimationBase {

    // Water drop images/elements
    private ImageView waterDropView;
    private Text wateringEmoji;

    /**
     * Create a new water animation for the specified cell
     * 
     * @param cell The StackPane where the animation will be shown
     */
    public WaterAnimation(StackPane cell) {
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
            // Try to load water drop image
            Image waterImage = new Image(getClass()
                    .getResourceAsStream("/com/example/project_csen_275/assests/Tiles/water-pour.png"));
            waterDropView = new ImageView(waterImage);
            waterDropView.setFitWidth(35);
            waterDropView.setFitHeight(35);
            waterDropView.setTranslateX(10);
            waterDropView.setTranslateY(-10);

            // Add to cell
            cell.getChildren().addAll(background, waterDropView);
        } catch (Exception ex) {
            // Fallback to emoji if image cannot be loaded
            GardenLogger.error("Failed to load water animation image: " + ex.getMessage());

            // Create watering can indicator with water drop emojis
            wateringEmoji = new Text("💧💦");
            wateringEmoji.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            wateringEmoji.setFill(Color.DEEPSKYBLUE);
            wateringEmoji.setTranslateX(10);
            wateringEmoji.setTranslateY(-10);

            // Add to cell
            cell.getChildren().addAll(background, wateringEmoji);
        }
    }

    @Override
    protected Timeline createTimeline() {
        Timeline timeline = new Timeline();

        if (waterDropView != null) {
            // Animation with image
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, _ -> {
                        waterDropView.setOpacity(0);
                        background.setOpacity(0);
                        waterDropView.setScaleX(0.8);
                        waterDropView.setScaleY(0.8);
                    }),
                    new KeyFrame(Duration.millis(100), _ -> {
                        waterDropView.setOpacity(1);
                        background.setOpacity(0.3);
                        waterDropView.setScaleX(1.0);
                        waterDropView.setScaleY(1.0);
                    }),
                    new KeyFrame(Duration.millis(300), _ -> {
                        waterDropView.setScaleX(1.2);
                        waterDropView.setScaleY(1.2);
                    }),
                    new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                        waterDropView.setOpacity(0);
                        background.setOpacity(0);
                        waterDropView.setScaleX(1.0);
                        waterDropView.setScaleY(1.0);
                    }),
                    new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));
        } else {
            // Animation with emoji
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, _ -> {
                        wateringEmoji.setOpacity(0);
                        background.setOpacity(0);
                    }),
                    new KeyFrame(Duration.millis(100), _ -> {
                        wateringEmoji.setOpacity(1);
                        background.setOpacity(0.5);
                    }),
                    new KeyFrame(Duration.millis(DISPLAY_DURATION - 200), _ -> {
                        wateringEmoji.setOpacity(0);
                        background.setOpacity(0);
                    }),
                    new KeyFrame(Duration.millis(DISPLAY_DURATION), _ -> cleanup()));
        }

        return timeline;
    }

    @Override
    protected void cleanup() {
        if (waterDropView != null) {
            cell.getChildren().removeAll(background, waterDropView);
        } else if (wateringEmoji != null) {
            cell.getChildren().removeAll(background, wateringEmoji);
        }
    }

    /**
     * Static convenience method to play water animation on a cell
     * 
     * @param cell The cell to animate
     */
    public static void play(StackPane cell) {
        new WaterAnimation(cell).play();
    }
}
