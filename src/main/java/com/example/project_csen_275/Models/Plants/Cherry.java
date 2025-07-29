package com.example.project_csen_275.Models.Plants;

/**
 * Cherry trees are fruit-bearing plants that need moderate water,
 * are sensitive to frost but can tolerate some drought.
 */
public class Cherry extends Plant {
    public Cherry() {
        super("Cherry", 100, 70, false, "cherry_small.png");
    }

    @Override
    public void dryOut() {
        super.dryOut();
        if (getMoistureLevel() > 60 && getHealth() > 80) {
            setImageUrl("cherry_grown.png");
        } else {
            setImageUrl("cherry_small.png");
        }
    }
    
    @Override
    protected double getDroughtResistanceMultiplier() {
        return 0.8; // Cherry trees have good drought resistance
    }
    
    @Override
    protected double getHeatResistanceMultiplier() {
        return 1.0; // Average heat tolerance
    }
    
    @Override
    protected double getColdResistanceMultiplier() {
        return 1.5; // Cherry trees are vulnerable to frost
    }
}
