package com.example.project_csen_275.Models.Plants;

public class Plant {
    private final String name;
    private int health;
    private int moistureLevel;
    private boolean hasPest;
    private String imageUrl;

    public Plant(String name, int health, int moistureLevel, boolean hasPest, String imageUrl) {
        this.name = name;
        this.health = health;
        this.moistureLevel = moistureLevel;
        this.hasPest = hasPest;
        this.imageUrl = imageUrl;
    }

    public Plant(String name, int health, int moistureLevel, boolean hasPest) {
        this(name, health, moistureLevel, hasPest, "grass.png");
    }

    public Plant(String name, int health, int moistureLevel) {
        this(name, health, moistureLevel, false);
    }

    public Plant(String name, int health) {
        this(name, health, 70);
    }

    public Plant(String name) {
        this(name, 100);
    }

    public void water() {
        moistureLevel = Math.min(moistureLevel + 20, 100);
    }

    public void applyPestDamage() {
        if (hasPest) {
            health = Math.max(0, health - 10);
        }
    }

    public void dryOut() {
        moistureLevel = Math.max(0, moistureLevel - 5);
        if (moistureLevel < 30) {
            health = Math.max(0, health - 5);
        }
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMoistureLevel() {
        return moistureLevel;
    }

    public boolean hasPest() {
        return hasPest;
    }

    public void setHasPest(boolean hasPest) {
        this.hasPest = hasPest;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}