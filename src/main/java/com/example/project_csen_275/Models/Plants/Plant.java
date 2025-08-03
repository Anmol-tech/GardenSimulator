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
     * Simulates the plant drying out, reducing moisture level and potentially causing health damage.
     * Plants with moisture level below 30% will take damage proportional to their dryness.
     * Different plants have different drought tolerance (override this method to customize).
     */
    public void dryOut() {
        // Drain moisture by 1 per cycle
        moistureLevel = Math.max(0, moistureLevel - 1);
        
        // Plants with low moisture take damage proportional to dryness
        if (moistureLevel < 30) {
            // More damage as moisture gets lower
            int damageFactor = (30 - moistureLevel) / 10 + 1;
            // Default drought resistance multiplier is 1.0
            health = Math.max(0, health - (int)(damageFactor * getDroughtResistanceMultiplier()));
        }
    }
    
    /**
     * Applies heat damage to the plant during a heat wave or sunny day event.
     * @param temperature The current temperature in °F
     * @return The amount of damage actually applied
     */
    public int applyHeatDamage(int temperature) {
        // Default behavior: 8 damage for hot temperatures
        int baseDamage = 8;
        int actualDamage = (int)(baseDamage * getHeatResistanceMultiplier());
        health = Math.max(0, health - actualDamage);
        return actualDamage;
    }
    
    /**
     * Applies cold damage to the plant during a frost or cold event.
     *
     * @param temperature The current temperature in °F
     */
    public void applyColdDamage(int temperature) {
        // Default behavior: 2 damage for cold temperatures
        int baseDamage = 2;
        int actualDamage = (int)(baseDamage * getColdResistanceMultiplier());
        health = Math.max(0, health - actualDamage);
    }
    
    /**
     * Gets the plant's resistance multiplier to drought (1.0 is normal, lower is more resistant)
     */
    protected double getDroughtResistanceMultiplier() {
        return 1.0; // Default multiplier
    }
    
    /**
     * Gets the plant's resistance multiplier to heat (1.0 is normal, lower is more resistant)
     */
    protected double getHeatResistanceMultiplier() {
        return 1.0; // Default multiplier
    }
    
    /**
     * Gets the plant's resistance multiplier to cold (1.0 is normal, lower is more resistant)
     */
    protected double getColdResistanceMultiplier() {
        return 1.0; // Default multiplier
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