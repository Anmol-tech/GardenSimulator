package com.example.project_csen_275;

import com.example.project_csen_275.Models.Garden;
import com.example.project_csen_275.Models.Plants.Plant;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

/**
 * Simulation API for programmatically seeding and controlling the garden.
 */
public class GardenSimulationAPI {
    private final Garden garden;
    private final PlantSelector plantSelector;
    // Configured plant metadata
    private static final Map<String,Integer> DEFAULT_WATER_REQUIREMENT = Map.of(
        "Carrot", 10,
        "Cherry", 12,
        "Corn", 15,
        "Pumpkin", 20,
        "Sunflower", 8
    );
    private static final Map<String,List<String>> DEFAULT_PARASITES = new HashMap<>();
    static {
        DEFAULT_PARASITES.put("Carrot", Arrays.asList("aphid","caterpillar"));
        DEFAULT_PARASITES.put("Cherry", Arrays.asList("bird","weevil"));
        DEFAULT_PARASITES.put("Corn", Arrays.asList("locust"));
        DEFAULT_PARASITES.put("Pumpkin", Arrays.asList("squashBug"));
        DEFAULT_PARASITES.put("Sunflower", Arrays.asList("aphid"));
        DEFAULT_PARASITES.put("Empty", Collections.emptyList());
    }
    // Lists to record initialization details
    private final List<String> plantNames = new ArrayList<>();
    private final List<Integer> waterRequirements = new ArrayList<>();
    private final List<List<String>> parasiteVulnerabilities = new ArrayList<>();

    /**
     * Constructs the API with a new garden of given dimensions.
     * @param rows number of rows in the garden grid
     * @param cols number of columns in the garden grid
     */
    public GardenSimulationAPI(int rows, int cols) {
        this.garden = new Garden(rows, cols);
        this.plantSelector = new PlantSelector();
        // clear metadata lists
        plantNames.clear();
        waterRequirements.clear();
        parasiteVulnerabilities.clear();
    }

    /**
     * Initializes the garden from a CSV config file (/garden_config.csv).
     * Each line should be: row,col,plantName
     */
    public void initializeGarden() {
        // Clear existing plants and stats
        garden.clearGarden();
        // Reset metadata
        plantNames.clear();
        waterRequirements.clear();
        parasiteVulnerabilities.clear();
        InputStream is = getClass().getResourceAsStream("/garden_config.csv");
        if (is == null) {
            GardenLogger.warning("Configuration file '/garden_config.csv' not found; skipping initialization.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length < 3) continue;
                int r = Integer.parseInt(parts[0]);
                int c = Integer.parseInt(parts[1]);
                String name = parts[2];
                // record metadata
                plantNames.add(name);
                waterRequirements.add(DEFAULT_WATER_REQUIREMENT.getOrDefault(name, 0));
                parasiteVulnerabilities.add(DEFAULT_PARASITES.getOrDefault(name, Collections.emptyList()));
                Plant plant = plantSelector.createPlantByName(name);
                garden.addPlant(r, c, plant);
            }
            GardenLogger.info("Garden initialized from config");
        } catch (Exception e) {
            GardenLogger.error("Failed to initialize garden: " + e.getMessage());
        }
    }

    /**
     * Returns metadata for the initialized plants: names, water requirements, and parasite lists.
     */
    public Map<String,Object> getPlants() {
        Map<String,Object> result = new HashMap<>();
        result.put("plants", new ArrayList<>(plantNames));
        result.put("waterRequirement", new ArrayList<>(waterRequirements));
        result.put("parasites", new ArrayList<>(parasiteVulnerabilities));
        return result;
    }

    /**
     * Returns the default parasite vulnerabilities for a given plant name.
     */
    public static List<String> getDefaultParasitesFor(String plantName) {
        return DEFAULT_PARASITES.getOrDefault(plantName, Collections.emptyList());
    }

    /**
     * Simulates rainfall in the garden by watering each plant (with health regen).
     * @param amount the water amount (ignored in this model)
     * @return number of plants watered
     */
    public int rain(int amount) {
        return garden.rain(amount);
    }

    /**
     * Simulates a temperature event in the garden.
     * @param temp the temperature in °F
     */
    public void temperature(int temp) {
        garden.temperature(temp);
    }

    /**
     * Triggers a parasite infestation by pest name across all vulnerable plants.
     * @param pestName the name of the parasite to infest
     * @return number of plants infested
     */
    /**
     * Helper to format pest names (camelCase to Title Case).
     */
    private static String formatPestName(String raw) {
        if (raw == null || raw.isEmpty()) {
            return raw;
        }
        String spaced = raw.replaceAll("([a-z])([A-Z])", "$1 $2");
        String[] parts = spaced.split("\\s+");
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String w = parts[i].toLowerCase();
            res.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
            if (i < parts.length - 1) res.append(" ");
        }
        return res.toString();
    }
    public int parasite(String pestName) {
        int count = 0;
        int rows = garden.getRows();
        int cols = garden.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Plant plant = garden.getPlant(r, c);
                if (!(plant instanceof com.example.project_csen_275.Models.Plants.NoPlant)
                        && plant.getHealth() > 0) {
                    List<String> vuln = DEFAULT_PARASITES.getOrDefault(plant.getName(), Collections.emptyList());
                    if (vuln.contains(pestName)) {
                        plant.setPestType(pestName);
                        count++;
                    }
                }
            }
        }
        // Format and log infestation without quotes
        String formatted = formatPestName(pestName);
        GardenLogger.warning("Parasite " + formatted + " infested " + count + " plants.");
        return count;
    }

    /**
     * Logs a summary of the garden's current state (stats and plant-type counts).
     */
    public void getState() {
        // Log header
        GardenLogger.info("=== Garden State Summary ===");
        // Core stats
        GardenLogger.info("Temperature: " + garden.getCurrentTemperature() + "°F");
        GardenLogger.info("Living Plants: " + garden.getLivePlantCount());
        GardenLogger.info("Dead Plants: " + garden.getDeadPlantCount());
        GardenLogger.info("Empty Soil: " + garden.getEmptySoilCount());
        GardenLogger.info("Plants Planted (total): " + garden.getPlantedCount());
        GardenLogger.info("Plants Watered (total): " + garden.getWateredCount());
        // Per-type breakdown
        Map<String, Integer> typeStats = garden.getPlantTypeStats();
        StringBuilder sb = new StringBuilder("Plant counts by type: ");
        typeStats.forEach((type, count) -> sb.append(type).append("=").append(count).append(", "));
        if (sb.length() > 0) sb.setLength(sb.length() - 2);
        GardenLogger.info(sb.toString());
    }

    /**
     * Provides the underlying Garden instance for further operations.
     * @return the Garden model
     */
    public Garden getGarden() {
        return garden;
    }
} 