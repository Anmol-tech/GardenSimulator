package com.example.project_csen_275.Models.Plants;

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
}
