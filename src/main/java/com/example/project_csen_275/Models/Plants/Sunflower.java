package com.example.project_csen_275.Models.Plants;

/**
 * Sunflowers are tall flowers that thrive in heat and sunshine,
 * are drought-tolerant but sensitive to cold.
 */
public class Sunflower extends Plant {
    public Sunflower() {
        super("Sunflower", 100, 70, false, "sunflower.png");
    }
    
    @Override
    protected double getDroughtResistanceMultiplier() {
        return 0.6; // Sunflowers are very drought resistant
    }
    
    @Override
    protected double getHeatResistanceMultiplier() {
        return 0.5; // Sunflowers love heat and thrive in it
    }
    
    @Override
    protected double getColdResistanceMultiplier() {
        return 1.6; // Sunflowers are sensitive to cold
    }
    
    /**
     * Sunflowers actually benefit from sunny days
     */
    @Override
    public int applyHeatDamage(int temperature) {
        if (temperature > 85) {
            // Only take damage in extreme heat
            return super.applyHeatDamage(temperature);
        } else if (temperature > 75) {
            // In moderate heat (sunny days), they actually gain health
            int healthBoost = 3;
            setHealth(Math.min(100, getHealth() + healthBoost));
            return -healthBoost; // Negative damage means health gained
        }
        return 0;
    }
}
