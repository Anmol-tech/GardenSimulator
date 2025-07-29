package com.example.project_csen_275.Models.Plants;

/**
 * Corn is a heat-loving grain that requires a lot of water,
 * tolerates heat very well but is sensitive to cold.
 */
public class Corn extends Plant {
    public Corn() {
        super("Corn", 100, 70, false, "stem.png");
    }

    @Override
    public void dryOut() {
        super.dryOut();
        if (getMoistureLevel() > 60 && getHealth() > 80) {
            setImageUrl("corn_grown.png");
        } else if (getHealth() < 20) {
            setImageUrl("dead_steam.png");
        } else {
            setImageUrl("stem.png");
        }
    }
    
    @Override
    protected double getDroughtResistanceMultiplier() {
        return 1.7; // Corn is very sensitive to drought
    }
    
    @Override
    protected double getHeatResistanceMultiplier() {
        return 0.6; // Corn loves heat and is resistant to it
    }
    
    @Override
    protected double getColdResistanceMultiplier() {
        return 1.8; // Corn is very sensitive to cold
    }
}
