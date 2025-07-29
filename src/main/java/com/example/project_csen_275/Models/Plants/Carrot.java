package com.example.project_csen_275.Models.Plants;

/**
 * Carrots are root vegetables that prefer moderate moisture,
 * tolerate cold well but are vulnerable to heat.
 */
public class Carrot extends Plant {
    public Carrot() {
        super("Carrot", 100, 70, false, "carrot_mid.png");
    }

    // Override dryOut to simulate carrot growth based on moisture
    @Override
    public void dryOut() {
        super.dryOut();
        // If moisture level is good, change image to grown carrot
        if (getMoistureLevel() > 60 && getHealth() > 80) {
            setImageUrl("carrot_grown.png");
        } else if (getMoistureLevel() < 30 || getHealth() < 50) {
            setImageUrl("carrot_radish_leaf.png");
        } else {
            setImageUrl("carrot_mid.png");
        }
    }
    
    @Override
    protected double getDroughtResistanceMultiplier() {
        return 1.2; // Carrots are more sensitive to drought than average
    }
    
    @Override
    protected double getHeatResistanceMultiplier() {
        return 1.5; // Carrots are very sensitive to heat
    }
    
    @Override
    protected double getColdResistanceMultiplier() {
        return 0.7; // Carrots tolerate cold relatively well
    }
}
