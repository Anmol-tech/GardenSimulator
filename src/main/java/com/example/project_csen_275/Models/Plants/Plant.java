package com.example.project_csen_275.Models.Plants;

public class Plant {
    private final String name;
    private int health;
    private int moistureLevel;
    private boolean hasPest;
    private String pestType;
    private int pestHealth;
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
        // Drain moisture by 1 per cycle
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
        // Initialize pest health when setting a new pest
        if (hasPest) {
            this.pestHealth = 20;
        } else {
            this.pestHealth = 0;
        }
    }
    /**
     * Gets current pest health.
     */
    public int getPestHealth() {
        return pestHealth;
    }
    /**
     * Sets current pest health.
     */
    public void setPestHealth(int health) {
        this.pestHealth = health;
        // Clear pest if health drops to zero
        if (health <= 0) {
            this.pestType = null;
            this.hasPest = false;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    /**
     * Sets the plant's health directly (0-100).
     */
    public void setHealth(int health) {
        this.health = health;
    }
}