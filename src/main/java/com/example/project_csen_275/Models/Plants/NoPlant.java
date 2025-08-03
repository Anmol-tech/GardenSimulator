package com.example.project_csen_275.Models.Plants;

public class NoPlant extends Plant {
    public NoPlant() {
        // Explicitly set health to 0 since this is empty soil
        super("Empty Soil", 0, 0, false, "tile_0000.png");
    }

    // Override water method to prevent watering empty soil
    @Override
    public void water() {
        // Do nothing - can't water empty soil
    }

    // Override dryOut to prevent health from changing for empty soil
    @Override
    public void dryOut() {
        // Do nothing - empty soil doesn't dry out
    }
    
    @Override
    public int applyHeatDamage(int temperature) {
        // Empty soil doesn't take heat damage
        return 0;
    }
    
    @Override
    public void applyColdDamage(int temperature) {
        // Empty soil doesn't take cold damage
    }
}
