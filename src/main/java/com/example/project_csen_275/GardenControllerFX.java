package com.example.project_csen_275;

import com.example.project_csen_275.Models.Garden;
import com.example.project_csen_275.GardenSimulationAPI;
import com.example.project_csen_275.Models.Plants.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GardenControllerFX implements Initializable {
    @FXML
    private GridPane gardenGrid;
    @FXML
    private ComboBox<String> plantTypeComboBox;
    @FXML
    private Text statusText;

    private final int ROWS = 5;
    private final int COLS = 5;
    private GardenSimulationAPI simApi;
    private Garden garden;
    private PlantSelector plantSelector;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private final Random random = new Random();

    // Stats display
    private VBox statsPanel;
    private Text livePlantsText = new Text("Living Plants: 0");
    private Text deadPlantsText = new Text("Dead Plants: 0");
    private Text emptyPlotsText = new Text("Empty Soil: 0");
    private Text plantedText = new Text("Plants Planted: 0");
    private Text wateredText = new Text("Plants Watered: 0");
    private Timeline statsUpdateTimer;

    // Log display
    private VBox logPanel;
    private javafx.scene.control.ListView<String> logListView;
    private Button clearLogsButton;

    // Automation properties
    @FXML
    private ToggleButton automationToggle;
    private Timeline automationTimer;
    private boolean isAutomationRunning = false;
    private final int AUTO_UPDATE_INTERVAL = 3; // seconds
    private int automationCycleCount = 0;
    // Batch-water logging fields
    private int waterBatchCount = 0;
    private Timeline waterLogTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize simulation API and garden from config
        simApi = new GardenSimulationAPI(ROWS, COLS);
        simApi.initializeGarden();
        garden = simApi.getGarden();
        // Initialize the plant selector
        plantSelector = new PlantSelector();

        // Set up the combo box with the same items as in the plant selector
        plantTypeComboBox.getItems().addAll(plantSelector.getComboBox().getItems());
        plantTypeComboBox.setValue("Empty");

        // Set up cell factory to show plant images in dropdown
        setupComboBoxCellFactory();

        // Initialize automation timer
        setupAutomationTimer();

        // Make sure automation button is properly styled from the start with high
        // visibility
        automationToggle.setText("▶️ START AUTOMATION");
        automationToggle.setStyle(
                "-fx-base: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5;");

        // Create stats panel
        setupStatsPanel();

        // Set up stats update timer
        setupStatsUpdateTimer();
        // Setup batch logging of watering every 10 seconds
        setupWaterBatchLogTimer();

        // Add listener to attach panels when scene is available
        gardenGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::attachPanelsToRoot);
            }
        });

        // Initialize the garden grid
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                StackPane cell = getStackPane(r, c);
                gardenGrid.add(cell, c, r);
            }
        }
        updateGrid();
        updateStats();
    }

    private StackPane getStackPane(int r, int c) {
        StackPane cell = new StackPane();
        cell.setMinSize(100, 100);
        cell.setPrefSize(100, 100);
        cell.setMaxSize(100, 100);
        cell.setStyle("-fx-border-color: #555555; -fx-background-color: #e8e8d0;");

        cell.setOnMouseClicked(e -> {
            // Set selected cell for planting
            selectedRow = r;
            selectedCol = c;
            statusText.setText("Selected position: Row " + (r + 1) + ", Column " + (c + 1));

            // If right-click, water the plant
            if (e.isSecondaryButtonDown()) {
                Plant plant = garden.getPlant(r, c);
                if (!(plant instanceof NoPlant)) {
                    garden.waterPlant(r, c);
                    statusText.setText("Watered plant at Row " + (r + 1) + ", Column " + (c + 1));
                    WaterAnimation.playWaterAnimation(cell);
                } else {
                    statusText.setText("Cannot water empty soil!");
                }
            }

            updateGrid();
            updateStats();
        });
        return cell;
    }

    @FXML
    public void onUpdate() {
        garden.updateGardenState();
        String message = "Garden updated! Plants have grown or changed.";
        statusText.setText(message);
        GardenLogger.info(message);
        updateGrid();
        updateStats();
    }

    @FXML
    public void onWaterAll() {
        int wateredCount = 0;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plant plant = garden.getPlant(r, c);

                // Only water and animate actual plants, not empty soil
                if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                    garden.waterPlant(r, c);
                    wateredCount++;

                    // Add water animation with delay based on position
                    final int row = r;
                    final int col = c;
                    StackPane cell = (StackPane) getNodeByRowColumnIndex(row, col);

                    // Create a delayed animation for each cell
                    Timeline delay = new Timeline(new KeyFrame(Duration.millis((r * COLS + c) * 80), e -> {
                        WaterAnimation.playWaterAnimation(cell);
                    }));
                    delay.play();
                }
            }
        }

        String message = "Watered " + wateredCount + " plants!";
        statusText.setText(message);
        GardenLogger.event(message);
        updateGrid();
        updateStats();
    }

    @FXML
    public void onPlantSelected() {
        if (selectedRow >= 0 && selectedCol >= 0) {
            String selectedPlantType = plantTypeComboBox.getValue();
            if (selectedPlantType != null) {
                // Create the plant from the selected type
                Plant newPlant;
                switch (selectedPlantType) {
                    case "Carrot":
                        newPlant = new Carrot();
                        break;
                    case "Cherry":
                        newPlant = new Cherry();
                        break;
                    case "Corn":
                        newPlant = new Corn();
                        break;
                    case "Pumpkin":
                        newPlant = new Pumpkin();
                        break;
                    case "Sunflower":
                        newPlant = new Sunflower();
                        break;
                    case "Empty":
                    default:
                        newPlant = new NoPlant();
                        break;
                }

                garden.addPlant(selectedRow, selectedCol, newPlant);
                String message = newPlant.getName() + " planted at Row " + (selectedRow + 1) + ", Column "
                        + (selectedCol + 1);
                statusText.setText(message);
                updateGrid();
                updateStats();
            } else {
                String message = "Please select a plant type first.";
                statusText.setText(message);
                GardenLogger.warning(message);
            }
        } else {
            String message = "Please select a grid position first by clicking on it.";
            statusText.setText(message);
            GardenLogger.warning(message);
        }
    }

    @FXML
    public void onAddPest() {
        // Add pest to a random plant
        int row = random.nextInt(ROWS);
        int col = random.nextInt(COLS);
        Plant plant = garden.getPlant(row, col);
        plant.setHasPest(true);
        String message = "A pest has appeared on " + plant.getName() + " at Row " + (row + 1) + ", Column " + (col + 1)
                + "!";
        statusText.setText(message);
        GardenLogger.warning(message);
        updateGrid();
        updateStats();
    }

    @FXML
    public void onRemovePests() {
        int count = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plant plant = garden.getPlant(r, c);
                if (plant.hasPest()) {
                    plant.setHasPest(false);
                    count++;
                    GardenLogger.info(
                            "Removed pest from " + plant.getName() + " at Row " + (r + 1) + ", Column " + (c + 1));
                }
            }
        }
        String message = "Removed " + count + " pests from the garden!";
        statusText.setText(message);
        GardenLogger.event(message);
        updateGrid();
        updateStats();
    }

    private void updateGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plant plant = garden.getPlant(r, c);
                StackPane cell = (StackPane) getNodeByRowColumnIndex(r, c);
                cell.getChildren().clear();

                // Determine the correct image
                String imagePath;

                if (plant instanceof NoPlant) {
                    // Empty soil - use basic soil tile
                    imagePath = "assests/Tiles/tile_0000.png";
                } else if (plant.getHealth() <= 0) {
                    // Dead plant (shouldn't happen as they should be converted to NoPlant)
                    imagePath = "assests/Tiles/dead_plant.png";
                } else {
                    // Normal plant image
                    imagePath = "assests/Tiles/" + plant.getImageUrl();
                }

                Image plantImage = new Image(getClass().getResourceAsStream(imagePath));
                ImageView imageView = new ImageView(plantImage);
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);

                // Special indicator for empty soil (NoPlant)
                if (plant instanceof NoPlant) {
                    Text soilText = new Text("🌱");
                    soilText.setOpacity(0.3); // Make it subtle
                    soilText.setTranslateY(-5);
                    cell.getChildren().add(soilText);
                }

                // Add visual indicator for health/pest status for living plants
                if (!(plant instanceof NoPlant)) {
                    // Create a background for the health text
                    Rectangle healthBg = new Rectangle(70, 24);
                    healthBg.setArcWidth(10);
                    healthBg.setArcHeight(10);
                    healthBg.setFill(Color.WHITE);
                    healthBg.setOpacity(0.9);
                    healthBg.setStroke(Color.DARKGRAY);
                    healthBg.setStrokeWidth(1.5);
                    healthBg.setTranslateX(0);
                    healthBg.setTranslateY(-38);

                    // Color based on health: green (good), yellow (medium), orange (low), red
                    // (pest)
                    Color healthColor;
                    String healthPrefix = "♥";
                    if (plant.hasPest()) {
                        healthColor = Color.RED;
                        healthPrefix = "🐛"; // Bug emoji for pests
                    } else if (plant.getHealth() > 75) {
                        healthColor = Color.GREEN;
                    } else if (plant.getHealth() > 50) {
                        healthColor = Color.GOLD;
                    } else {
                        healthColor = Color.ORANGE;
                    }

                    // Add health label with the health value
                    Label healthLabel = new Label(healthPrefix + " " + plant.getHealth());
                    healthLabel.setTextFill(healthColor);
                    healthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    healthLabel.setTranslateX(0);
                    healthLabel.setTranslateY(-38);

                    // Add moisture indicator with better visibility
                    Label moistureLabel = new Label("💧 " + plant.getMoistureLevel());
                    moistureLabel.setTextFill(Color.DEEPSKYBLUE);
                    moistureLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    moistureLabel.setTranslateX(0);
                    moistureLabel.setTranslateY(38);

                    // Add indicators to the cell, making sure they're on top of other elements
                    cell.getChildren().add(healthBg);
                    cell.getChildren().addAll(healthLabel, moistureLabel);
                }

                cell.getChildren().add(imageView);

                // Highlight selected cell
                if (r == selectedRow && c == selectedCol) {
                    cell.setStyle("-fx-border-color: blue; -fx-border-width: 2px; -fx-background-color: #e8e8d0;");
                } else {
                    cell.setStyle("-fx-border-color: #555555; -fx-background-color: #e8e8d0;");
                }
            }
        }
    }

    private StackPane getNodeByRowColumnIndex(int row, int column) {
        for (javafx.scene.Node node : gardenGrid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return (StackPane) node;
            }
        }
        return null;
    }

    private void setupAutomationTimer() {
        // Create a timeline for automatic garden updates
        automationTimer = new Timeline(new KeyFrame(Duration.seconds(AUTO_UPDATE_INTERVAL), event -> {
            // Increment cycle count
            automationCycleCount++;

            // Perform automatic update
            garden.updateGardenState();

            // Consolidate automatic watering count
            int autoWaterCount = 0;

            // Random watering every 6 cycles (~18s)
            if (automationCycleCount % 6 == 0) {
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        // 25% chance to water each plant when triggered
                        if (!(plant instanceof NoPlant) && plant.getHealth() > 0 && random.nextInt(4) == 0) {
                            garden.waterPlantSilently(r, c);
                            autoWaterCount++;
                            final int row = r;
                            final int col = c;
                            Platform.runLater(() -> {
                                StackPane cell = (StackPane) getNodeByRowColumnIndex(row, col);
                                WaterAnimation.playWaterAnimation(cell);
                            });
                        }
                    }
                }
            }

            // Accumulate watering for batch log
            waterBatchCount += autoWaterCount;

            // Every 200 cycles (~10 minutes), water all plants
            if (automationCycleCount % 200 == 0) {
                Platform.runLater(this::onWaterAll);
            }

            // Randomly plant new plants in empty soil (15% chance per cycle)
            if (random.nextInt(7) == 0) { // ~15% chance
                boolean planted = garden.plantRandomPlant();
                if (planted) {
                    Platform.runLater(() -> {
                        statusText.setText("A new plant has been planted in an empty spot!");
                    });
                }
            }

            // Random pest addition (5% chance)
            if (random.nextInt(20) == 0) { // 5% chance
                int row = random.nextInt(ROWS);
                int col = random.nextInt(COLS);
                Plant plant = garden.getPlant(row, col);
                if (!plant.hasPest() && !(plant instanceof NoPlant)) {
                    plant.setHasPest(true);
                    Platform.runLater(() -> {
                        statusText.setText("A pest appeared on " + plant.getName() + " at Row " + (row + 1)
                                + ", Column " + (col + 1) + "!");
                    });
                }
            }

            // Every 5 cycles, trigger a random event
            if (automationCycleCount % 5 == 0) {
                Platform.runLater(this::triggerRandomEvent);
            }

            // Update UI
            Platform.runLater(() -> {
                updateGrid();
                updateStats();
            });
        }));

        // Set cycle count to indefinite to run forever
        automationTimer.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    public void onToggleAutomation() {
        if (isAutomationRunning) {
            waterLogTimer.stop();
            // Stop automation
            automationTimer.stop();
            isAutomationRunning = false;
            automationToggle.setText("▶️ Start Automation");
            automationToggle.setStyle(
                    "-fx-base: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5;");
            String message = "Garden automation stopped";
            statusText.setText(message);
            GardenLogger.info(message);
        } else {
            waterLogTimer.play();
            // Start automation
            automationTimer.play();
            isAutomationRunning = true;
            automationToggle.setText("⏹️ Stop Automation");
            automationToggle.setStyle(
                    "-fx-base: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5;");
            String message = "Garden automation running - updates every " + AUTO_UPDATE_INTERVAL + " seconds";
            statusText.setText(message);
            GardenLogger.info(message);

            // Initial random event to make it interesting
            triggerRandomEvent();
        }
    }

    private void triggerRandomEvent() {
        // Generate a random event in the garden
        int eventType = random.nextInt(5); // 5 different event types

        switch (eventType) {
            case 0: // Sunny day - water evaporates faster
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        garden.getPlant(r, c).dryOut(); // Extra drying
                    }
                }
                String message = "It's a ☀️sunny day! Plants are drying faster.";
                statusText.setText(message);
                GardenLogger.event(message);
                break;

            case 1: // Rainy day - use API rain to water all plants with health regen
                // Delegate to simulation API for rain
                int rainCount = simApi.rain(0); // amount ignored in model; uses plant.water()
                // Animate all watered cells
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                            final int row = r;
                            final int col = c;
                            Platform.runLater(() -> {
                                StackPane cell = getNodeByRowColumnIndex(row, col);
                                WaterAnimation.playWaterAnimation(cell);
                            });
                        }
                    }
                }
                String rainMessage = "It's raining! " + rainCount + " plants have been watered.";
                statusText.setText(rainMessage);
                GardenLogger.event(rainMessage);
                break;

            case 2: // Pest infestation - random pests appear based on vulnerabilities
                int pestCount = 0;
                // Track infestation summary (optional)
                Map<String,Integer> infestSummary = new HashMap<>();
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        if (!(plant instanceof NoPlant) && random.nextInt(3) == 0) { // 33% chance
                            // choose a pest from vulnerabilities
                            List<String> pests = GardenSimulationAPI.getDefaultParasitesFor(plant.getName());
                            if (!pests.isEmpty()) {
                                String pest = pests.get(random.nextInt(pests.size()));
                                plant.setPestType(pest);
                                pestCount++;
                                infestSummary.put(pest, infestSummary.getOrDefault(pest, 0) + 1);
                            }
                        }
                    }
                }
                // Build message listing pest types
                StringBuilder msg = new StringBuilder("Oh no! Pest infestation: ");
                msg.append(pestCount).append(" plants were infested");
                if (!infestSummary.isEmpty()) {
                    msg.append(" (");
                    infestSummary.forEach((p, count) -> msg.append(p).append(" x").append(count).append(", "));
                    // remove trailing comma and space
                    msg.setLength(msg.length() - 2);
                    msg.append(")");
                }
                String pestMessage = msg.toString();
                statusText.setText(pestMessage);
                GardenLogger.warning(pestMessage);
                break;

            case 3: // Perfect growth conditions - plants get healthier
                int healthyPlantCount = 0;
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        // Only water actual plants, not empty soil
                        if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                            // No direct way to increase health, so we'll water them well
                            garden.waterPlant(r, c);
                            garden.waterPlant(r, c);
                            healthyPlantCount++;
                        }
                    }
                }
                String growthMessage = "Perfect 🌸growing conditions today! " + healthyPlantCount
                        + " plants are thriving.";
                statusText.setText(growthMessage);
                GardenLogger.info(growthMessage);
                break;

            case 4: // Gardener visit - remove pests and water plants
                int pestsRemoved = 0;
                int plantsWatered = 0;
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);

                        // Only care about actual plants, not empty soil
                        if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                            if (plant.hasPest()) {
                                plant.setHasPest(false);
                                pestsRemoved++;
                            }
                            garden.waterPlant(r, c);
                            plantsWatered++;
                        }
                    }
                }
                String gardenerMessage = "A gardener visited! Removed " + pestsRemoved + " pests and watered "
                        + plantsWatered + " plants.";
                statusText.setText(gardenerMessage);
                GardenLogger.info(gardenerMessage);
                break;
        }

        updateGrid();
    }

    /**
     * Set up the stats panel to display garden statistics
     */
    private void setupStatsPanel() {
        // Create stats panel
        statsPanel = new VBox(8);
        statsPanel.setStyle(
                "-fx-padding: 15px; -fx-background-color: #f0f8ff; -fx-border-color: #b3e5fc; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        statsPanel.setMinWidth(300);
        statsPanel.setPrefWidth(300);

        // Add title with better styling
        Text statsTitle = new Text("Garden Statistics");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        statsTitle.setFill(Color.ROYALBLUE);
        statsPanel.getChildren().add(statsTitle);

        // Add horizontal separator
        Separator separator = new Separator();
        separator.setPrefWidth(280);
        statsPanel.getChildren().add(separator);

        // Add stats texts with better styling and icons
        livePlantsText.setText("🌿 Living Plants: 0");
        livePlantsText.setFill(Color.GREEN);
        livePlantsText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        deadPlantsText.setText("💀 Dead Plants: 0");
        deadPlantsText.setFill(Color.DARKRED);
        deadPlantsText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        emptyPlotsText.setText("🌱 Empty Soil: 0");
        emptyPlotsText.setFill(Color.BROWN);
        emptyPlotsText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        plantedText.setText("🪴 Plants Planted: 0");
        plantedText.setFill(Color.BLUE);
        plantedText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        wateredText.setText("💧 Plants Watered: 0");
        wateredText.setFill(Color.DEEPSKYBLUE);
        wateredText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        statsPanel.getChildren().addAll(
                livePlantsText,
                deadPlantsText,
                emptyPlotsText,
                plantedText,
                wateredText);

        // Set up the log panel
        setupLogPanel();

        // We'll attach the panels to the root BorderPane after the scene is available
    }

    /**
     * Attach the stats and log panels to the root BorderPane
     */
    private void attachPanelsToRoot() {
        // Create a VBox to hold both panels
        VBox rightPanels = new VBox(10);
        rightPanels.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        rightPanels.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        rightPanels.getChildren().addAll(statsPanel, logPanel);

        // Find the root BorderPane and set the right side
        BorderPane borderPane = (BorderPane) gardenGrid.getScene().getRoot();
        borderPane.setRight(rightPanels);
    }

    /**
     * Set up the log panel to display garden operation logs
     */
    private void setupLogPanel() {
        // Create log panel with improved styling
        logPanel = new VBox(8);
        logPanel.setStyle(
                "-fx-padding: 15px; -fx-background-color: #fff8f0; -fx-border-color: #ffcc80; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        logPanel.setMinWidth(300);
        logPanel.setPrefWidth(300);
        VBox.setVgrow(logPanel, Priority.ALWAYS);

        // Add title with better styling
        Text logTitle = new Text("📝 Garden Activity Log");
        logTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        logTitle.setFill(Color.DARKORANGE);

        // Add separator
        Separator separator = new Separator();
        separator.setPrefWidth(280);

        // Create log list view with better styling
        logListView = new javafx.scene.control.ListView<>();
        logListView.setItems(GardenLogger.getLogs());
        logListView.setPrefHeight(250); // Make it taller
        logListView.setStyle(
                "-fx-background-color: #fffaf0; -fx-background-radius: 5px; -fx-border-color: #ffe0b2; -fx-border-radius: 5px;");
        VBox.setVgrow(logListView, Priority.ALWAYS);

        // Create styled clear logs button
        clearLogsButton = new Button("🗑️ Clear Logs");
        clearLogsButton.setStyle("-fx-base: #ffcc80; -fx-font-weight: bold;");
        clearLogsButton.setOnAction(e -> {
            GardenLogger.clearLogs();
            GardenLogger.info("Logs cleared");
        });

        // Add components to log panel
        logPanel.getChildren().addAll(logTitle, separator, logListView, clearLogsButton);

        // Log initial message
        GardenLogger.info("Garden application started");
    }

    /**
     * Set up a timer to periodically update the stats
     */
    private void setupStatsUpdateTimer() {
        statsUpdateTimer = new Timeline(new KeyFrame(Duration.seconds(2), event -> updateStats()));
        statsUpdateTimer.setCycleCount(Timeline.INDEFINITE);
        statsUpdateTimer.play();
    }

    /**
     * Update the garden statistics display
     */
    private void updateStats() {
        // Count plant types
        int liveCount = garden.getLivePlantCount();
        int emptyCount = garden.getEmptySoilCount();
        int deadCount = garden.getDeadPlantCount();
        int plantedCount = garden.getPlantedCount();
        int wateredCount = garden.getWateredCount();

        // Update text nodes
        livePlantsText.setText("Living Plants: " + liveCount);
        deadPlantsText.setText("Dead Plants: " + deadCount);
        emptyPlotsText.setText("Empty Soil: " + emptyCount);
        plantedText.setText("Plants Planted: " + plantedCount);
        wateredText.setText("Plants Watered: " + wateredCount);
    }

    /**
     * Sets up a timer to log total waterings in batch every 10 seconds.
     */
    private void setupWaterBatchLogTimer() {
        waterLogTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            if (waterBatchCount > 0) {
                GardenLogger.info("Watered " + waterBatchCount + " plants in the last 10 seconds");
                waterBatchCount = 0;
            }
        }));
        waterLogTimer.setCycleCount(Timeline.INDEFINITE);
    }

    private void setupComboBoxCellFactory() {
        plantTypeComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // Create plant instance to get the image URL
                        Plant plant = null;
                        switch (item) {
                            case "Carrot":
                                plant = new Carrot();
                                break;
                            case "Cherry":
                                plant = new Cherry();
                                break;
                            case "Corn":
                                plant = new Corn();
                                break;
                            case "Pumpkin":
                                plant = new Pumpkin();
                                break;
                            case "Sunflower":
                                plant = new Sunflower();
                                break;
                            case "Empty":
                            default:
                                plant = new NoPlant();
                                break;
                        }

                        if (plant != null) {
                            String imagePath = "assests/Tiles/" + plant.getImageUrl();
                            Image image = new Image(getClass().getResourceAsStream(imagePath));
                            imageView.setImage(image);
                            imageView.setFitHeight(20);
                            imageView.setFitWidth(20);
                            setText(item);
                            setGraphic(imageView);
                        } else {
                            setText(item);
                            setGraphic(null);
                        }
                    } catch (Exception e) {
                        setText(item);
                        setGraphic(null);
                    }
                }
            }
        });

        // Also set the button cell to show the selected image
        plantTypeComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        // Create plant instance to get the image URL
                        Plant plant = null;
                        switch (item) {
                            case "Carrot":
                                plant = new Carrot();
                                break;
                            case "Cherry":
                                plant = new Cherry();
                                break;
                            case "Corn":
                                plant = new Corn();
                                break;
                            case "Pumpkin":
                                plant = new Pumpkin();
                                break;
                            case "Sunflower":
                                plant = new Sunflower();
                                break;
                            case "Empty":
                            default:
                                plant = new NoPlant();
                                break;
                        }

                        if (plant != null) {
                            String imagePath = "assests/Tiles/" + plant.getImageUrl();
                            Image image = new Image(getClass().getResourceAsStream(imagePath));
                            imageView.setImage(image);
                            imageView.setFitHeight(20);
                            imageView.setFitWidth(20);
                            setText(item);
                            setGraphic(imageView);
                        } 
                        else {
                            setText(item);
                            setGraphic(null);
                        }
                    } catch (Exception e) {
                        setText(item);
                        setGraphic(null);
                    }
                }
            }
        });
    }
}
