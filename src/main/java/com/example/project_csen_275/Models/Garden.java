package com.example.project_csen_275.Models;

import com.example.project_csen_275.Models.Plants.*;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class Garden {
    private final Plant[][] grid;
    private final Random random = new Random();
    // Delay cycles before replanting after death
    private final int[][] replantDelay;
    private static final int REPLANT_DELAY_CYCLES = 3;

    // Stats tracking
    private int deadPlantCount = 0;
    private int plantedCount = 0;
    private int wateredCount = 0;
    // Temperature tracking (default to ideal 70°F)
    private int currentTemperature = 70;

    public Garden(int rows, int cols) {
        grid = new Plant[rows][cols];
        replantDelay = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = createRandomPlant();
                replantDelay[r][c] = -1; // no replant scheduled
            }
        }
    }

    private Plant createRandomPlant() {
        int plantType = random.nextInt(7); // 0-6
        return switch (plantType) {
            case 1 -> new Carrot();
            case 2 -> new Cherry();
            case 3 -> new Corn();
            case 4 -> new Pumpkin();
            case 5 -> new Sunflower();
            default -> new NoPlant();
        };
    }

    public void addPlant(int row, int col, Plant plant) {
        grid[row][col] = plant;

        // If planting a real plant (not NoPlant), increment planted count
        if (!(plant instanceof NoPlant)) {
            plantedCount++;
            // Log planting operation
            if (com.example.project_csen_275.GardenLogger.class != null) {
                com.example.project_csen_275.GardenLogger
                        .info("Planted " + plant.getName() + " at position [" + row + "," + col + "]");
            }
        } else {
            // Log clearing operation
            if (com.example.project_csen_275.GardenLogger.class != null) {
                com.example.project_csen_275.GardenLogger.info("Cleared position [" + row + "," + col + "]");
            }
        }
    }

    public Plant getPlant(int row, int col) {
        return grid[row][col];
    }

    public int getRows() {
        return grid.length;
    }

    public int getCols() {
        return grid[0].length;
    }

    public void waterPlant(int row, int col) {
        Plant plant = grid[row][col];
        // Only water actual plants (not empty soil) that are alive (health > 0)
        if (plant != null && !(plant instanceof NoPlant) && plant.getHealth() > 0) {
            plant.water();
            wateredCount++;
        }
    }

    // Silent watering without logging, for automated batch operations
    public void waterPlantSilently(int row, int col) {
        Plant plant = grid[row][col];
        if (plant != null && !(plant instanceof NoPlant) && plant.getHealth() > 0) {
            plant.water();
            wateredCount++;
        }
    }

    public void updateGardenState() {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                Plant plant = grid[r][c];
                if (plant != null) {
                    // Only update living plants
                    if (!(plant instanceof NoPlant)) {
                        plant.dryOut();
                        // Apply pest damage only 50% of the time when a pest is present
                        if (plant.hasPest() && random.nextInt(2) == 0) {
                            plant.applyPestDamage();
                        }

                        // Convert dead plants to empty soil (NoPlant)
                        if (plant.getHealth() <= 0) {
                            String plantName = plant.getName();
                            grid[r][c] = new NoPlant();
                            deadPlantCount++;
                            replantDelay[r][c] = REPLANT_DELAY_CYCLES; // schedule replant
                            // Log plant death
                            if (com.example.project_csen_275.GardenLogger.class != null) {
                                com.example.project_csen_275.GardenLogger.warning(
                                    plantName + " at position [" + r + "," + c + "] died and will respawn soon");
                            }
                        }
                    }
                    // NoPlant instances have health 0 by default, so we don't need to set it here
                }
            }
        }
        // Handle scheduled replanting
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] instanceof NoPlant && replantDelay[r][c] >= 0) {
                    replantDelay[r][c]--;
                    if (replantDelay[r][c] == 0) {
                        // Plant a new random plant in this spot
                        Plant newPlant;
                        int type = random.nextInt(5) + 1; // 1-5 for real plants
                        newPlant = switch (type) {
                            case 2 -> new Cherry();
                            case 3 -> new Corn();
                            case 4 -> new Pumpkin();
                            case 5 -> new Sunflower();
                            default -> new Carrot();
                        };
                        grid[r][c] = newPlant;
                        plantedCount++;
                        // Log automatic planting
                        if (com.example.project_csen_275.GardenLogger.class != null) {
                            com.example.project_csen_275.GardenLogger.info(
                                "Automatically planted " + newPlant.getName() + " at position [" + r + "," + c + "]");
                        }
                        replantDelay[r][c] = -1;
                    }
                }
            }
        }
    }

    /**
     * Simulates rainfall by adding given amount of water to all plants.
     * @return number of plants watered
     */
    public int rain() {
        int count = 0;
        for (Plant[] plants : grid) {
            for (Plant plant : plants) {
                if (plant != null && !(plant instanceof NoPlant) && plant.getHealth() > 0) {
                    plant.water(); // standard water + health regen
                    wateredCount++;
                    count++;
                }
            }
        }
        if (com.example.project_csen_275.GardenLogger.class != null) {
            com.example.project_csen_275.GardenLogger.event(
                "Rainfall: watered " + count + " plants.");
        }
        return count;
    }

    /**
     * Applies a temperature event to the garden, causing heat or cold stress.
     * Different plants respond differently to temperature changes.
     * @param temp the temperature in °F
     */
    public void temperature(int temp) {
        this.currentTemperature = temp;
        int affected = 0;
        if (temp > 75) {
            // Heat stress: extra drying and heat damage
            for (Plant[] plants : grid) {
                for (Plant plant : plants) {
                    if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                        plant.dryOut(); // Extra drying based on plant type
                        // Apply heat damage based on plant's heat resistance
                        plant.applyHeatDamage(temp);
                        affected++;
                    }
                }
            }
            if (com.example.project_csen_275.GardenLogger.class != null) {
                if (javafx.application.Platform.isFxApplicationThread()) {
                    // We're on the FX thread, safe to update UI
                    com.example.project_csen_275.GardenLogger.warning("Heat wave! " + affected + " plants affected by high temperature (" + temp + "°F) - different plants respond differently");
                } else {
                    // We're not on FX thread, use Platform.runLater
                    final int finalAffected = affected;
                    final int finalTemp = temp;
                    javafx.application.Platform.runLater(() -> com.example.project_csen_275.GardenLogger.warning("Heat wave! " + finalAffected + " plants affected by high temperature (" + finalTemp + "°F) - different plants respond differently"));
                }
            }
        } else if (temp < 65) {
            // Cold stress: damage health based on plant's cold resistance
            for (Plant[] plants : grid) {
                for (Plant plant : plants) {
                    if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                        plant.applyColdDamage(temp);
                        affected++;
                    }
                }
            }
            if (com.example.project_csen_275.GardenLogger.class != null) {
                if (javafx.application.Platform.isFxApplicationThread()) {
                    // We're on the FX thread, safe to update UI
                    com.example.project_csen_275.GardenLogger.warning("Frost damage! " + affected + " plants lost health due to low temperature (" + temp + "°F)");
                } else {
                    // We're not on FX thread, use Platform.runLater
                    final int finalAffected = affected;
                    final int finalTemp = temp;
                    javafx.application.Platform.runLater(() -> {
                        com.example.project_csen_275.GardenLogger.warning("Frost damage! " + finalAffected + " plants lost health due to low temperature (" + finalTemp + "°F)");
                    });
                }
            }
        } else {
            // Ideal temperature
            if (com.example.project_csen_275.GardenLogger.class != null) {
                if (javafx.application.Platform.isFxApplicationThread()) {
                    // We're on the FX thread, safe to update UI
                    com.example.project_csen_275.GardenLogger.event("Ideal temperature: " + temp + "°F. No stress applied.");
                } else {
                    // We're not on FX thread, use Platform.runLater
                    final int finalTemp = temp;
                    javafx.application.Platform.runLater(() -> {
                        com.example.project_csen_275.GardenLogger.event("Ideal temperature: " + finalTemp + "°F. No stress applied.");
                    });
                }
            }
        }
    }
    
    /**
     * Updates temperature value without logging or UI updates.
     * Safe to call from any thread.
     * @param temp the new temperature value
     */
    public void setTemperature(int temp) {
        this.currentTemperature = temp;
    }

    /**
     * Gets the last applied temperature.
     */
    public int getCurrentTemperature() {
        return currentTemperature;
    }

    /**
     * Clears all plants from the garden and resets stats.
     */
    public void clearGarden() {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                grid[r][c] = new NoPlant();
            }
        }
        deadPlantCount = 0;
        plantedCount = 0;
        wateredCount = 0;
        if (com.example.project_csen_275.GardenLogger.class != null) {
            com.example.project_csen_275.GardenLogger.info("Garden cleared for initialization");
        }
    }

    // Add methods to get garden stats
    public int getDeadPlantCount() {
        return deadPlantCount;
    }

    public int getPlantedCount() {
        return plantedCount;
    }

    public int getWateredCount() {
        return wateredCount;
    }

    /**
     * Get the count of each type of plant currently in the garden
     * 
     * @return A map with plant names as keys and counts as values
     */
    public Map<String, Integer> getPlantTypeStats() {
        Map<String, Integer> stats = new HashMap<>();

        for (Plant[] plants : grid) {
            for (Plant plant : plants) {
                if (plant != null) {
                    String plantName = plant.getName();
                    stats.put(plantName, stats.getOrDefault(plantName, 0) + 1);
                }
            }
        }

        return stats;
    }

    /**
     * Get the total count of living plants (excluding empty soil)
     */
    public int getLivePlantCount() {
        int count = 0;

        for (Plant[] plants : grid) {
            for (Plant plant : plants) {
                if (plant != null && !(plant instanceof NoPlant)) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Get the count of empty soil plots
     */
    public int getEmptySoilCount() {
        int count = 0;

        for (Plant[] plants : grid) {
            for (Plant plant : plants) {
                if (plant instanceof NoPlant) {
                    count++;
                }
            }
        }

        return count;
    }
}
