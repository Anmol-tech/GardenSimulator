package com.example.project_csen_275.Models.Plants;

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
}
