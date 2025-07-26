package com.example.project_csen_275.Models.Plants;

public class Plant {
    private final String name;
    private int health;
    private int moistureLevel;
    private boolean hasPest;
    private String pestType;
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
        health = Math.min(health + 5, 100);
    }

    public void applyPestDamage() {
        if (hasPest) {
            health = Math.max(0, health - 10);
        }
    }

    /**
     * Adds a specified amount of water to the plant's moisture level (no health restoration).
     * @param amount water amount to add
     */
    public void addWater(int amount) {
        moistureLevel = Math.min(moistureLevel + amount, 100);
    }

    public void dryOut() {
        // Slow moisture drain to 1 per cycle
        moistureLevel = Math.max(0, moistureLevel - 1);
        if (moistureLevel < 30) {
            health = Math.max(0, health - 2);
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
        return pestType != null && !pestType.isEmpty();
    }

    public void setHasPest(boolean hasPest) {
        if (!hasPest) {
            this.pestType = null;
        }
    }

    /**
     * Gets the specific pest type infesting this plant.
     */
    public String getPestType() {
        return pestType;
    }

    /**
     * Sets the pest type for this plant. Passing null or empty clears the pest.
     */
    public void setPestType(String pestType) {
        this.pestType = pestType;
        this.hasPest = (pestType != null && !pestType.isEmpty());
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}