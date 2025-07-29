package com.example.project_csen_275.animations;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Base class for all garden animations
 */
public abstract class AnimationBase {

    // Common animation duration in milliseconds
    protected static final int DISPLAY_DURATION = 800;

    // Background rectangle for better visibility
    protected Rectangle background;

    // Cell where the animation will be displayed
    protected StackPane cell;

    /**
     * Create a new animation for the specified cell
     * 
     * @param cell The StackPane where the animation will be shown
     */
    public AnimationBase(StackPane cell) {
        this.cell = cell;

        // Create common background
        background = new Rectangle(40, 40);
        background.setFill(Color.WHITE);
        background.setOpacity(0.3);
        background.setArcWidth(10);
        background.setArcHeight(10);
        background.setTranslateX(10);
        background.setTranslateY(-10);
    }

    /**
     * Play the animation
     */
    public abstract void play();

    /**
     * Create the animation timeline
     * 
     * @return The animation timeline
     */
    protected abstract Timeline createTimeline();

    /**
     * Prepare the animation elements
     */
    protected abstract void prepareElements();

    /**
     * Clean up the animation elements
     */
    protected abstract void cleanup();
}
