package com.example.project_csen_275.Models;

import com.example.project_csen_275.Models.Plants.*;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class Garden {
    private Plant[][] grid;
    private Random random = new Random();

    // Stats tracking
    private int deadPlantCount = 0;
    private int plantedCount = 0;
    private int wateredCount = 0;
    // Temperature tracking (default to ideal 70°F)
    private int currentTemperature = 70;

    public Garden(int rows, int cols) {
        grid = new Plant[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = createRandomPlant();
            }
        }
    }

    private Plant createRandomPlant() {
        int plantType = random.nextInt(7); // 0-6
        switch (plantType) {
            case 0:
                return new NoPlant();
            case 1:
                return new Carrot();
            case 2:
                return new Cherry();
            case 3:
                return new Corn();
            case 4:
                return new Pumpkin();
            case 5:
                return new Sunflower();
            default:
                return new NoPlant();
        }
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

                            // Log plant death
                            if (com.example.project_csen_275.GardenLogger.class != null) {
                                com.example.project_csen_275.GardenLogger.warning(plantName + " at position [" + r + ","
                                        + c + "] died and was replaced with soil");
                            }
                        }
                    }
                    // NoPlant instances have health 0 by default, so we don't need to set it here
                }
            }
        }
    }

    /**
     * Simulates rainfall by adding given amount of water to all plants.
     * @param amount the water amount to add per plant
     * @return number of plants watered
     */
    public int rain(int amount) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                Plant plant = grid[r][c];
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
     * Plants above 90°F dry out once; below 60°F take 5 health damage.
     * @param temp the temperature in °F
     */
    public void temperature(int temp) {
        this.currentTemperature = temp;
        int affected = 0;
        if (temp > 75) {
            // Heat stress: extra drying
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    Plant plant = grid[r][c];
                    if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                        plant.dryOut(); // Extra drying
                        // Additional heat damage: -8 health
                        plant.setHealth(Math.max(0, plant.getHealth() - 8));
                        affected++;
                    }
                }
            }
            if (com.example.project_csen_275.GardenLogger.class != null) {
                com.example.project_csen_275.GardenLogger.warning("Heat wave! " + affected + " plants dried out and took 8 damage due to high temperature (" + temp + "°F)");
            }
        } else if (temp < 65) {
            // Cold stress: damage health
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    Plant plant = grid[r][c];
                    if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                        int newHealth = Math.max(0, plant.getHealth() - 2);
                        plant.setHealth(newHealth);
                        affected++;
                    }
                }
            }
            if (com.example.project_csen_275.GardenLogger.class != null) {
                com.example.project_csen_275.GardenLogger.warning("Frost damage! " + affected + " plants lost health due to low temperature (" + temp + "°F)");
            }
        } else {
            // Ideal temperature
            if (com.example.project_csen_275.GardenLogger.class != null) {
                com.example.project_csen_275.GardenLogger.event("Ideal temperature: " + temp + "°F. No stress applied.");
            }
        }
    }

    /**
     * Gets the last applied temperature.
     */
    public int getCurrentTemperature() {
        return currentTemperature;
    }

    /**
     * Try to plant a new plant in an empty soil spot
     * 
     * @return true if a new plant was planted, false otherwise
     */
    public boolean plantRandomPlant() {
        // Find empty spots in the garden
        int rows = grid.length;
        int cols = grid[0].length;

        // Create arrays to store empty spot coordinates
        int[] emptyRows = new int[rows * cols];
        int[] emptyCols = new int[rows * cols];
        int emptyCount = 0;

        // Find all empty spots
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] instanceof NoPlant) {
                    emptyRows[emptyCount] = r;
                    emptyCols[emptyCount] = c;
                    emptyCount++;
                }
            }
        }

        // If there are empty spots, plant a random plant in one of them
        if (emptyCount > 0) {
            int randomSpot = random.nextInt(emptyCount);
            int row = emptyRows[randomSpot];
            int col = emptyCols[randomSpot];

            // Create a random plant (excluding NoPlant)
            int plantType = random.nextInt(5) + 1; // 1-5
            Plant newPlant;

            switch (plantType) {
                case 1:
                    newPlant = new Carrot();
                    break;
                case 2:
                    newPlant = new Cherry();
                    break;
                case 3:
                    newPlant = new Corn();
                    break;
                case 4:
                    newPlant = new Pumpkin();
                    break;
                case 5:
                    newPlant = new Sunflower();
                    break;
                default:
                    newPlant = new Carrot();
                    break;
            }

            // Plant the new plant
            grid[row][col] = newPlant;
            plantedCount++;

            // Log automatic planting
            if (com.example.project_csen_275.GardenLogger.class != null) {
                com.example.project_csen_275.GardenLogger
                        .info("Automatically planted " + newPlant.getName() + " at position [" + row + "," + col + "]");
            }

            return true;
        }

        return false;
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

    /**
     * Reset the counters for stats that accumulate over time
     */
    public void resetStats() {
        deadPlantCount = 0;
        plantedCount = 0;
        wateredCount = 0;
    }
}
