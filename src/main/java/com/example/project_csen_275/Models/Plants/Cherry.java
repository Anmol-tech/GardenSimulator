package com.example.project_csen_275.Models.Plants;

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
}
