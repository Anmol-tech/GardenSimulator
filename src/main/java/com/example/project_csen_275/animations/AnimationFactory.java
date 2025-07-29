package com.example.project_csen_275.animations;

import javafx.scene.layout.StackPane;

/**
 * Factory class for creating and playing garden animations
 */
public class AnimationFactory {

    /**
     * Types of animations available
     */
    public enum AnimationType {
        WATER,
        PEST_SPRAY,
        FROST,
        RAIN,
        SUNSHINE,
        FARMER
    }

    /**
     * Play an animation on the specified cell based on the animation type
     * 
     * @param cell The StackPane where the animation will be shown
     * @param type The type of animation to play
     */
    public static void playAnimation(StackPane cell, AnimationType type) {
        switch (type) {
            case WATER:
                WaterAnimation.play(cell);
                break;
            case PEST_SPRAY:
                PestSprayAnimation.play(cell);
                break;
            case FROST:
                FrostAnimation.play(cell);
                break;
            case RAIN:
                RainAnimation.play(cell);
                break;
            case SUNSHINE:
                SunshineAnimation.play(cell);
                break;
            case FARMER:
                FarmerAnimation.play(cell);
                break;
            default:
                // No animation played
                break;
        }
    }

    /**
     * Create a specific animation instance without playing it
     * 
     * @param cell The StackPane where the animation will be shown
     * @param type The type of animation to create
     * @return The created animation instance
     */
    public static AnimationBase createAnimation(StackPane cell, AnimationType type) {
        switch (type) {
            case WATER:
                return new WaterAnimation(cell);
            case PEST_SPRAY:
                return new PestSprayAnimation(cell);
            case FROST:
                return new FrostAnimation(cell);
            case RAIN:
                return new RainAnimation(cell);
            case SUNSHINE:
                return new SunshineAnimation(cell);
            case FARMER:
                return new FarmerAnimation(cell);
            default:
                throw new IllegalArgumentException("Unsupported animation type: " + type);
        }
    }
}
