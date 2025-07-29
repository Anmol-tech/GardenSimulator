package com.example.project_csen_275.Models.Plants;

/**
 * Pumpkins are large gourds that need significant water,
 * and have moderate resistance to both heat and cold.
 */
public class Pumpkin extends Plant {
    public Pumpkin() {
        super("Pumpkin", 100, 70, false, "pumpkin_small.png");
    }

    @Override
    public void dryOut() {
        super.dryOut();
        if (getMoistureLevel() > 60 && getHealth() > 80) {
            setImageUrl("pumpkin_full_grow.png");
        } else {
            setImageUrl("pumpkin_small.png");
        }

        // If health is critical, show dying plant
        if (getHealth() < 20) {
            setImageUrl("dead_plant.png");
        }
    }
    
    @Override
    protected double getDroughtResistanceMultiplier() {
        return 1.3; // Pumpkins need lots of water
    }
    
    @Override
    protected double getHeatResistanceMultiplier() {
        return 0.8; // Pumpkins have good heat resistance
    }
    
    @Override
    protected double getColdResistanceMultiplier() {
        return 0.9; // Pumpkins have decent cold resistance
    }
}
