package com.example.project_csen_275.Models.Plants;

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
}
