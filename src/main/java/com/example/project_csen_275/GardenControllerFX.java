package com.example.project_csen_275;

import com.example.project_csen_275.Models.Garden;
import com.example.project_csen_275.GardenSimulationAPI;
import com.example.project_csen_275.Models.Plants.*;
import com.example.project_csen_275.animations.AnimationFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ScrollPane;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class GardenControllerFX implements Initializable {
    @FXML
    private GridPane gardenGrid;
    @FXML
    private Button helpButton;
    @FXML
    private ComboBox<String> plantTypeComboBox;
    @FXML
    private Text statusText;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private ImageView eventImageView; // For displaying event icons

    // For manual event triggering
    private int forcedEventType = -1;

    // Event icon images
    private Image sunEventImage;
    private Image frostEventImage;

    private final int ROWS = 5;
    private final int COLS = 5;
    private GardenSimulationAPI simApi;
    private Garden garden;
    private PlantSelector plantSelector;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private final Random random = new Random();
    // Pest spray defense
    private boolean sprayPending = false;
    private boolean sprayActive = false;
    private int sprayCyclesLeft = 0;
    private static final int SPRAY_INITIAL_DMG = 8;
    private static final int SPRAY_SUBSEQUENT_DMG = 3;

    // Thread pool for background operations with saturation protection
    private final ExecutorService gardenExecutor = new ThreadPoolExecutor(
            3, // Core pool size
            3, // Maximum pool size (same as core for fixed size)
            0L, TimeUnit.MILLISECONDS, // Keep-alive time for excess threads
            new LinkedBlockingQueue<Runnable>(), // Work queue
            new ThreadFactory() {
                private final AtomicInteger threadCount = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "GardenThread-" + threadCount.getAndIncrement());
                    thread.setDaemon(true); // Don't prevent JVM shutdown
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // Saturation policy - run in caller's thread if queue is full
    );
    // Temperature regulation
    private static final int IDEAL_TEMP_LOWER = 65;
    private static final int IDEAL_TEMP_UPPER = 75;
    private boolean insulationCoverActive = false;
    private boolean insulationPending = false; // Delay cover activation
    private int insulationCyclesLeft = 0;

    // Stats display
    private VBox statsPanel;
    private Text livePlantsText = new Text("🌿 Living Plants: 0");
    private Text deadPlantsText = new Text("�� Dead Plants: 0");
    private Text emptyPlotsText = new Text("🌱 Empty Soil: 0");
    private Text plantedText = new Text("🪴 Plants Planted: 0");
    private Text wateredText = new Text("💧 Plants Watered: 0");
    private Text temperatureText = new Text("Temperature: 0°F");
    @FXML
    private Text gameTimeText;
    @FXML
    private Text sessionTimeText;
    private GardenTimer gardenTimer;
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
    private Timeline hourlyReportTimer;

    /**
     * Handles exceptions in a centralized manner
     * 
     * @param ex      The exception to handle
     * @param message A user-friendly message to display
     */
    private void handleException(Throwable ex, String message) {
        // Log the error
        GardenLogger.error(message + " - Error: " + ex.getMessage());

        // Show error dialog on the JavaFX thread
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Garden Application Error");
            alert.setHeaderText(message);
            alert.setContentText("Error details: " + ex.getMessage());
            alert.showAndWait();

            // Update status
            statusText.setText("Error: " + message);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize simulation API and garden from config
            simApi = new GardenSimulationAPI(ROWS, COLS);
            simApi.initializeGarden();
            garden = simApi.getGarden();
            // Initialize the plant selector
            plantSelector = new PlantSelector();

            // Set up the combo box with the same items as in the plant selector
            plantTypeComboBox.getItems().addAll(plantSelector.getComboBox().getItems());
            plantTypeComboBox.setValue("Empty");

            // Set up the event dropdown for manual triggering
            eventComboBox.getItems().addAll(
                    "Sunny Day", "Rainy Day", "Pest Infestation",
                    "Perfect Growth", "Gardener Visit", "Chilly Day");
            eventComboBox.setValue("Sunny Day");

            // Load event icons and hide by default
            sunEventImage = new Image(getClass().getResourceAsStream("assests/Tiles/sun.png"));
            frostEventImage = new Image(getClass().getResourceAsStream("assests/Tiles/frost.png"));
            eventImageView.setVisible(false);

            // Set up cell factory to show plant images in dropdown
            setupComboBoxCellFactory();

            // Initialize automation timer
            setupAutomationTimer();

            // Make sure automation button is properly styled from the start with high
            // visibility
            automationToggle.setText("▶️ START AUTOMATION");
            automationToggle.setStyle(
                    "-fx-base: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10px 20px; -fx-border-radius: 5;");

            // Initialize garden timer
            gardenTimer = new GardenTimer();
            gameTimeText.textProperty().bind(gardenTimer.timeStringProperty());
            sessionTimeText.textProperty().bind(gardenTimer.sessionTimeStringProperty());
            gardenTimer.start();

            // Create stats panel
            setupStatsPanel();

            // Set up stats update timer
            setupStatsUpdateTimer();
            // Setup batch logging of watering every 10 seconds
            setupWaterBatchLogTimer();
            // Setup real-time hourly state reporting
            setupHourlyReportTimer();

            // Add listener to attach panels when scene is available
            gardenGrid.sceneProperty().addListener((scene, oldScene, newScene) -> {
                // Using named parameters for clarity though they're not used
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
            // Ensure Help button is visible and on top
            helpButton.setVisible(true);
            helpButton.toFront();
            updateStats();
        } catch (Exception ex) {
            handleException(ex, "Failed to initialize garden application");
        }
    }

    private StackPane getStackPane(int r, int c) {
        StackPane cell = new StackPane();
        cell.setMinSize(80, 80);
        cell.setPrefSize(80, 80);
        cell.setMaxSize(80, 80);
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
                    AnimationFactory.playAnimation(cell, AnimationFactory.AnimationType.WATER);
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
        try {
            // Update status first
            String message = "Garden updating...";
            statusText.setText(message);

            // Run the garden update in a background thread
            gardenExecutor.submit(() -> {
                try {
                    // Perform the garden update operation
                    garden.updateGardenState();

                    // Update UI on JavaFX thread when complete
                    Platform.runLater(() -> {
                        try {
                            String successMessage = "Garden updated! Plants have grown or changed.";
                            statusText.setText(successMessage);
                            GardenLogger.info(successMessage);
                            updateGrid();
                            updateStats();
                        } catch (Exception ex) {
                            handleException(ex, "Error updating UI after garden update");
                        }
                    });
                } catch (Exception ex) {
                    handleException(ex, "Error updating garden state");
                }
            });
        } catch (Exception ex) {
            handleException(ex, "Failed to start garden update");
        }
    }

    @FXML
    public void onWaterAll() {
        try {
            // Show watering in progress status
            statusText.setText("Watering plants in progress...");

            // Create and submit watering task
            gardenExecutor.submit(() -> {
                try {
                    // Track plants to water for animations
                    final java.util.List<int[]> plantsToWater = new java.util.ArrayList<>();
                    final java.util.concurrent.atomic.AtomicInteger wateredCount = new java.util.concurrent.atomic.AtomicInteger(
                            0);

                    // First pass - identify plants and water them (fast operation)
                    for (int r = 0; r < ROWS; r++) {
                        for (int c = 0; c < COLS; c++) {
                            Plant plant = garden.getPlant(r, c);

                            // Only water actual plants, not empty soil
                            if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                                garden.waterPlant(r, c);
                                wateredCount.incrementAndGet();
                                plantsToWater.add(new int[] { r, c });
                            }
                        }
                    }

                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        try {
                            // Play animations with proper timing
                            for (int i = 0; i < plantsToWater.size(); i++) {
                                int[] pos = plantsToWater.get(i);
                                StackPane cell = (StackPane) getNodeByRowColumnIndex(pos[0], pos[1]);

                                // Create a delayed animation for each cell
                                final int index = i; // For closure
                                Timeline delay = new Timeline(new KeyFrame(Duration.millis(index * 80),
                                        e -> AnimationFactory.playAnimation(cell,
                                                AnimationFactory.AnimationType.WATER)));
                                delay.play();
                            }

                            // Update status and UI
                            String message = "Watered " + wateredCount.get() + " plants!";
                            statusText.setText(message);
                            GardenLogger.event(message);
                            updateGrid();
                            updateStats();
                        } catch (Exception ex) {
                            handleException(ex, "Error updating UI after watering");
                        }
                    });
                } catch (Exception ex) {
                    handleException(ex, "Error during watering operation");
                }
            });
        } catch (Exception ex) {
            handleException(ex, "Failed to start watering operation");
        }
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
        try {
            // Add a specific pest to a random vulnerable plant
            boolean added = false;
            for (int attempts = 0; attempts < 10 && !added; attempts++) {
                int row = random.nextInt(ROWS);
                int col = random.nextInt(COLS);
                Plant plant = garden.getPlant(row, col);
                if (!(plant instanceof NoPlant) && plant.getHealth() > 0 && !plant.hasPest()) {
                    // Pick a random pest based on vulnerability
                    List<String> pests = GardenSimulationAPI.getDefaultParasitesFor(plant.getName());
                    if (!pests.isEmpty()) {
                        String pestName = pests.get(random.nextInt(pests.size()));
                        plant.setPestType(pestName);
                        // Schedule spray next cycle
                        sprayPending = true;
                        String msg = "Parasite '" + pestName + "' added to " + plant.getName() +
                                " at Row " + (row + 1) + ", Column " + (col + 1);
                        statusText.setText(msg);
                        GardenLogger.warning(msg);
                        added = true;
                    }
                }
            }
            if (!added) {
                String noMsg = "No suitable plants found for pest infestation!";
                statusText.setText(noMsg);
                GardenLogger.warning(noMsg);
            }

            // Update UI
            updateGrid();
            updateStats();
        } catch (Exception ex) {
            handleException(ex, "Error adding pest to garden");
        }
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
        // Stop any ongoing or scheduled pest sprays
        sprayPending = false;
        sprayActive = false;
        sprayCyclesLeft = 0;
        updateGrid();
        updateStats();
    }

    @FXML
    public void onTriggerEvent() {
        try {
            String selected = eventComboBox.getValue();
            if (selected == null) {
                statusText.setText("Please select an event first.");
                return;
            }
            switch (selected) {
                case "Sunny Day":
                    forcedEventType = 0;
                    break;
                case "Rainy Day":
                    forcedEventType = 1;
                    break;
                case "Pest Infestation":
                    forcedEventType = 2;
                    break;
                case "Perfect Growth":
                    forcedEventType = 3;
                    break;
                case "Gardener Visit":
                    forcedEventType = 4;
                    break;
                case "Chilly Day":
                    forcedEventType = 5;
                    break;
                default:
                    statusText.setText("Unknown event: " + selected);
                    return;
            }
            // Trigger the chosen event immediately
            triggerRandomEvent();
        } catch (Exception ex) {
            handleException(ex, "Error triggering event");
        }
    }

    @FXML
    public void onHelp() {
        try {
            Stage helpStage = new Stage();
            helpStage.setTitle("Garden Simulator Help");
            // Create styled container
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));
            root.setAlignment(Pos.TOP_LEFT);
            root.setStyle("-fx-background-color: linear-gradient(#e8ffea, #c8f8cc); " +
                    "-fx-border-color: #80c080; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: transparent;");
            // Title
            Label title = new Label("🎮 Garden Simulator Help");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
            title.setWrapText(true);
            root.getChildren().add(title);
            // Sections with styled headings and descriptions
            // Plant Selector
            Label plantHdr = new Label("Plant Selector:");
            plantHdr.setStyle(
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #204020;");
            root.getChildren().add(plantHdr);
            Label plantDesc = new Label(
                    "Choose a plant from the dropdown and click 'Plant Selected' to place it in the grid. " +
                            "Right-click on a cell to water individual plants manually.");
            plantDesc.setWrapText(true);
            plantDesc.setMaxWidth(360);
            plantDesc.setStyle(
                    "-fx-background-color: rgba(255,255,128,0.3); -fx-font-size: 13px; -fx-text-fill: #204020; -fx-padding: 4px;");
            root.getChildren().add(plantDesc);
            // Water All
            Label waterHdr = new Label("Water All:");
            waterHdr.setStyle(
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #204020;");
            root.getChildren().add(waterHdr);
            Label waterDesc = new Label(
                    "Water every plant in the garden, restoring moisture and up to +9 health per action.");
            waterDesc.setWrapText(true);
            waterDesc.setMaxWidth(360);
            waterDesc.setStyle(
                    "-fx-background-color: rgba(255,255,128,0.3); -fx-font-size: 13px; -fx-text-fill: #204020; -fx-padding: 4px;");
            root.getChildren().add(waterDesc);
            // Add Pests
            Label pestHdr = new Label("Add Pests:");
            pestHdr.setStyle(
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #204020;");
            root.getChildren().add(pestHdr);
            Label pestDesc = new Label(
                    "Spawn a random pest on a vulnerable plant. Pests have 20 health and damage plants each cycle. " +
                            "Pest Spray defense auto-deploys next cycle to deal 8 damage initially, then 3 per cycle.");
            pestDesc.setWrapText(true);
            pestDesc.setMaxWidth(360);
            pestDesc.setStyle(
                    "-fx-background-color: rgba(255,255,128,0.3); -fx-font-size: 13px; -fx-text-fill: #204020; -fx-padding: 4px;");
            root.getChildren().add(pestDesc);
            // Remove Bugs
            Label removeHdr = new Label("Remove Bugs:");
            removeHdr.setStyle(
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #204020;");
            root.getChildren().add(removeHdr);
            Label removeDesc = new Label(
                    "A gardener visits, removes all pests instantly, and waters every plant for a health boost. " +
                            "This stops any ongoing pest spray.");
            removeDesc.setWrapText(true);
            removeDesc.setMaxWidth(360);
            removeDesc.setStyle(
                    "-fx-background-color: rgba(255,255,128,0.3); -fx-font-size: 13px; -fx-text-fill: #204020; -fx-padding: 4px;");
            root.getChildren().add(removeDesc);
            // Automation
            Label autoHdr = new Label("Automation:");
            autoHdr.setStyle(
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #204020;");
            root.getChildren().add(autoHdr);
            Label autoDesc = new Label(
                    "Toggle automatic mode (every 3s): cycles apply garden updates, random events, pest spray, and " +
                            "randomly waters plants (25% chance every 18s).");
            autoDesc.setWrapText(true);
            autoDesc.setMaxWidth(360);
            autoDesc.setStyle(
                    "-fx-background-color: rgba(255,255,128,0.3); -fx-font-size: 13px; -fx-text-fill: #204020; -fx-padding: 4px;");
            root.getChildren().add(autoDesc);
            // Events Modeled
            Label eventsHdr = new Label("Events Modeled:");
            eventsHdr.setStyle(
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #204020;");
            root.getChildren().add(eventsHdr);
            Label eventsDesc = new Label(
                    "Sunny Day: extra moisture loss + heat stress;\n" +
                            "Rainy Day: rainwaters all plants + regen;\n" +
                            "Pest Infestation: random pests appear;\n" +
                            "Perfect Growth: double water, restores ideal temp;\n" +
                            "Gardener Visit: clears pests & waters;\n" +
                            "Chilly Day: temp drop + delayed insulation cover to restore heat.");
            eventsDesc.setWrapText(true);
            eventsDesc.setMaxWidth(360);
            eventsDesc.setStyle(
                    "-fx-background-color: rgba(255,255,128,0.3); -fx-font-size: 13px; -fx-text-fill: #204020; -fx-padding: 4px;");
            root.getChildren().add(eventsDesc);
            // Detailed Mechanics
            Label mechHdr = new Label("Detailed Mechanics:");
            mechHdr.setStyle(
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #204020;");
            root.getChildren().add(mechHdr);
            Label mechDesc = new Label(
                    "- Chilly Day: -2 health damage per cycle when temp <65°F. Insulation cover engages next cycle to raise +1°F per cycle until ideal.\n"
                            +
                            "- Sunny Day: -8 health damage per cycle when temp >75°F.\n" +
                            "- Watering: restores moisture and +9 health manually or via rain.\n" +
                            "- Pest Spray: initial 8 damage then 3 per cycle for up to 5 cycles, triggers 1 cycle after infestation.\n"
                            +
                            "- Gardener Visit: removes pests & waters all plants, canceling sprays.\n" +
                            "- Automation: runs every 3s, manages updates, events, sprays, and random watering.");
            mechDesc.setWrapText(true);
            mechDesc.setMaxWidth(360);
            mechDesc.setStyle(
                    "-fx-background-color: rgba(255,255,128,0.3); -fx-font-size: 13px; -fx-text-fill: #204020; -fx-padding: 4px;");
            root.getChildren().add(mechDesc);
            Scene scene = new Scene(scrollPane, 380, 260);
            helpStage.setScene(scene);
            helpStage.initOwner(gardenGrid.getScene().getWindow());
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.show();
        } catch (Exception ex) {
            handleException(ex, "Error opening Help window");
        }
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
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);

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
                    Rectangle healthBg = new Rectangle(60, 20);
                    healthBg.setArcWidth(8);
                    healthBg.setArcHeight(8);
                    healthBg.setFill(Color.WHITE);
                    healthBg.setOpacity(0.9);
                    healthBg.setStroke(Color.DARKGRAY);
                    healthBg.setStrokeWidth(1.5);
                    healthBg.setTranslateX(0);
                    healthBg.setTranslateY(-30);

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
                    healthLabel.setTranslateY(-32);

                    // Add moisture indicator with better visibility
                    Label moistureLabel = new Label("💧 " + plant.getMoistureLevel());
                    moistureLabel.setTextFill(Color.DEEPSKYBLUE);
                    moistureLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    moistureLabel.setTranslateX(0);
                    moistureLabel.setTranslateY(32);

                    // Add indicators to the cell, making sure they're on top of other elements
                    cell.getChildren().add(healthBg);
                    cell.getChildren().addAll(healthLabel, moistureLabel);
                }

                if (plant.hasPest()) {
                    // Show pest health overlay (smaller, moved down to fit within cell)
                    Rectangle pestBg = new Rectangle(50, 14);
                    pestBg.setArcWidth(8);
                    pestBg.setArcHeight(8);
                    pestBg.setFill(Color.WHITE);
                    pestBg.setStroke(Color.RED);
                    pestBg.setStrokeWidth(1.5);
                    pestBg.setOpacity(0.9);
                    pestBg.setTranslateX(0);
                    pestBg.setTranslateY(-48);

                    Label pestLabel = new Label("P: " + plant.getPestHealth());
                    pestLabel.setTextFill(Color.RED);
                    pestLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 9px;");
                    pestLabel.setTranslateX(0);
                    pestLabel.setTranslateY(-48);

                    cell.getChildren().addAll(pestBg, pestLabel);
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
        try {
            // Create a timeline for automatic garden updates
            automationTimer = new Timeline(
                    new KeyFrame(Duration.seconds(AUTO_UPDATE_INTERVAL),
                            event -> handleAutomationUpdate()));

            // Set cycle count to indefinite to run forever
            automationTimer.setCycleCount(Timeline.INDEFINITE);
        } catch (Exception ex) {
            handleException(ex, "Failed to set up automation timer");
        }
    }

    /**
     * Handle a single automation update cycle with error handling and
     * multithreading
     */
    private void handleAutomationUpdate() {
        try {
            // Increment cycle count
            automationCycleCount++;

            // Use thread pool to handle garden update in background
            gardenExecutor.submit(() -> {
                try {
                    // Perform automatic update
                    garden.updateGardenState();

                    // Count of plants auto-watered this cycle
                    int autoWaterCount = 0;
                    // If a pest was just added last cycle, schedule spray
                    if (sprayPending) {
                        sprayActive = true;
                        sprayCyclesLeft = 5;
                        sprayPending = false;
                        String deployMsg = "Deploying Anti-pest spray for " + sprayCyclesLeft + " cycles!";
                        // Update UI and log on JavaFX thread
                        Platform.runLater(() -> {
                            statusText.setText(deployMsg);
                            GardenLogger.event(deployMsg);
                        });
                    }

                    // Execute spray if active, collect positions to animate after UI update
                    java.util.List<int[]> sprayedPositions = new java.util.ArrayList<>();
                    if (sprayActive && sprayCyclesLeft > 0) {
                        for (int r = 0; r < ROWS; r++) {
                            for (int c = 0; c < COLS; c++) {
                                Plant plant = garden.getPlant(r, c);
                                if (plant.hasPest()) {
                                    int dmg = sprayCyclesLeft == 5 ? SPRAY_INITIAL_DMG : SPRAY_SUBSEQUENT_DMG;
                                    int newPestHealth = plant.getPestHealth() - dmg;
                                    plant.setPestHealth(newPestHealth);
                                    sprayedPositions.add(new int[] { r, c });
                                }
                            }
                        }
                        sprayCyclesLeft--;
                        if (sprayCyclesLeft == 0) {
                            sprayActive = false;
                            final String endMsg = "Pest spray ended.";
                            Platform.runLater(() -> {
                                statusText.setText(endMsg);
                                GardenLogger.event(endMsg);
                            });
                        }
                    }

                    // Random watering every 6 cycles (~18s)
                    if (automationCycleCount % 6 == 0) {
                        for (int r = 0; r < ROWS; r++) {
                            for (int c = 0; c < COLS; c++) {
                                Plant plant = garden.getPlant(r, c);
                                if (!(plant instanceof NoPlant) && plant.getHealth() > 0 && random.nextInt(4) == 0) {
                                    garden.waterPlantSilently(r, c);
                                    autoWaterCount++;
                                }
                            }
                        }
                    }

                    // Accumulate watering for batch log
                    waterBatchCount += autoWaterCount;

                    // Random pest addition (approx 2% chance)
                    if (random.nextInt(50) == 0) { // ~2% chance
                        int row = random.nextInt(ROWS);
                        int col = random.nextInt(COLS);
                        Plant plant = garden.getPlant(row, col);
                        if (!(plant instanceof NoPlant) && !plant.hasPest() && plant.getHealth() > 0) {
                            // Choose a random pest from this plant's vulnerabilities
                            List<String> pests = GardenSimulationAPI.getDefaultParasitesFor(plant.getName());
                            if (!pests.isEmpty()) {
                                String pestName = pests.get(random.nextInt(pests.size()));
                                plant.setPestType(pestName);
                                // Schedule pest spray next cycle
                                sprayPending = true;
                                // Log the infestation event
                                String logMsg = "Parasite '" + pestName + "' appeared on " + plant.getName() +
                                        " at Row " + (row + 1) + ", Column " + (col + 1);
                                GardenLogger.warning(logMsg);
                                // Update UI status
                                Platform.runLater(() -> statusText.setText(logMsg));
                            }
                        }
                    }

                    // Handle pest addition (approx 2% chance)
                    handleAutomationPests();

                    // Apply temperature effects
                    handleAutomationTemperature();

                    // Every 5 cycles, trigger a random event
                    if (automationCycleCount % 5 == 0) {
                        Platform.runLater(this::triggerRandomEvent);
                    }

                    // Update UI, then play pest spray animations for this cycle
                    Platform.runLater(() -> {
                        try {
                            updateGrid();
                            updateStats();
                            // Play pest spray animation on sprayed positions
                            for (int[] pos : sprayedPositions) {
                                StackPane cell = getNodeByRowColumnIndex(pos[0], pos[1]);
                                if (cell != null) {
                                    AnimationFactory.playAnimation(cell, AnimationFactory.AnimationType.PEST_SPRAY);
                                }
                            }
                        } catch (Exception ex) {
                            GardenLogger.error("Error updating UI in automation: " + ex.getMessage());
                        }
                    });
                } catch (Exception ex) {
                    handleException(ex, "Automation update failed");
                }
            });
        } catch (Exception ex) {
            handleException(ex, "Failed to submit automation update task");
        }
    }

    /**
     * Handle pest addition during automation with proper error handling
     */
    private void handleAutomationPests() {
        try {
            // Random pest addition (approx 2% chance)
            if (random.nextInt(50) == 0) { // ~2% chance
                int row = random.nextInt(ROWS);
                int col = random.nextInt(COLS);
                Plant plant = garden.getPlant(row, col);
                if (!(plant instanceof NoPlant) && !plant.hasPest() && plant.getHealth() > 0) {
                    try {
                        // Choose a random pest from this plant's vulnerabilities
                        List<String> pests = GardenSimulationAPI.getDefaultParasitesFor(plant.getName());
                        if (!pests.isEmpty()) {
                            String pestName = pests.get(random.nextInt(pests.size()));
                            plant.setPestType(pestName);

                            // Store information for logging on UI thread
                            final String finalPestName = pestName;
                            final String plantName = plant.getName();
                            final int finalRow = row;
                            final int finalCol = col;

                            // Execute UI updates on the JavaFX application thread
                            Platform.runLater(() -> {
                                try {
                                    // Log the infestation event
                                    String logMsg = "Parasite '" + finalPestName + "' appeared on " + plantName +
                                            " at Row " + (finalRow + 1) + ", Column " + (finalCol + 1);
                                    GardenLogger.warning(logMsg);

                                    // Update UI status
                                    statusText.setText(logMsg);
                                } catch (Exception e) {
                                    // Handle any exceptions in the UI thread
                                    handleException(e, "Error updating UI after pest infestation");
                                }
                            });
                        }
                    } catch (Exception ex) {
                        final String errorMsg = "Error adding pest: " + ex.getMessage();
                        Platform.runLater(() -> GardenLogger.error(errorMsg));
                    }
                }
            }
        } catch (Exception ex) {
            final String errorMsg = "Error handling pests in automation: " + ex.getMessage();
            Platform.runLater(() -> GardenLogger.error(errorMsg));
        }
    }

    /**
     * Handle temperature effects during automation with proper error handling
     */
    private void handleAutomationTemperature() {
        try {
            // Activate delayed insulation cover if pending
            if (insulationPending) {
                insulationCoverActive = true;
                insulationPending = false;
                Platform.runLater(
                        () -> GardenLogger.event("Insulation cover engaged for " + insulationCyclesLeft + " cycles."));
            }
            // Apply frost stress penalty every cycle if below ideal
            final int temp = garden.getCurrentTemperature();
            if (temp < IDEAL_TEMP_LOWER) {
                int penaltyCount = 0;
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        try {
                            Plant plant = garden.getPlant(r, c);
                            if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                                int newH = Math.max(0, plant.getHealth() - 2);
                                plant.setHealth(newH);
                                penaltyCount++;
                            }
                        } catch (Exception ex) {
                            final String errorMsg = "Error applying temperature effect to plant at " +
                                    r + "," + c + ": " + ex.getMessage();
                            Platform.runLater(() -> GardenLogger.error(errorMsg));
                        }
                    }
                }
                final int finalPenaltyCount = penaltyCount;
                Platform.runLater(() -> GardenLogger
                        .warning(finalPenaltyCount + " plants took -2 health due to low temperature (" + temp + "°F)"));
            }

            // Insulation cover effect: restore 1°F per cycle until ideal is reached
            if (insulationCoverActive) {
                try {
                    final int currentTemp = garden.getCurrentTemperature();
                    if (currentTemp < IDEAL_TEMP_LOWER) {
                        final int newTemp = Math.min(currentTemp + 1, IDEAL_TEMP_LOWER);
                        // Update temperature without UI updates
                        garden.setTemperature(newTemp);
                        final int tempToSet = newTemp;
                        Platform.runLater(() -> {
                            try {
                                garden.temperature(tempToSet);
                            } catch (Exception e) {
                                GardenLogger.error("Error setting temperature in FX thread: " + e.getMessage());
                            }
                        });
                    } else {
                        // Ideal reached; disable insulation cover
                        insulationCoverActive = false;
                        Platform.runLater(() -> GardenLogger
                                .event("Insulation cover effect ended – temperature regulation normal."));
                    }
                } catch (Exception ex) {
                    final String errorMsg = "Error applying insulation effect: " + ex.getMessage();
                    Platform.runLater(() -> GardenLogger.error(errorMsg));
                }
            }
        } catch (Exception ex) {
            final String errorMsg = "Error handling temperature in automation: " + ex.getMessage();
            Platform.runLater(() -> GardenLogger.error(errorMsg));
        }
    }

    // This duplicate method was removed

    @FXML
    public void onToggleAutomation() {
        if (isAutomationRunning) {
            // Check for null before stopping
            if (waterLogTimer != null) {
                waterLogTimer.stop();
            }
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
            // Check for null before playing
            if (waterLogTimer != null) {
                waterLogTimer.play();
            }
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
        // Hide any previous event icon
        eventImageView.setVisible(false);

        // Determine event type: forcedEventType if set, else random
        int eventType;
        if (forcedEventType >= 0) {
            eventType = forcedEventType;
            forcedEventType = -1;
        } else {
            eventType = random.nextInt(6); // 6 different event types (add chilly day)
        }

        switch (eventType) {
            case 0: // Sunny day - extra drying and temperature rise
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        garden.getPlant(r, c).dryOut(); // Extra drying
                    }
                }
                // Increase temperature above ideal range (e.g., 76-85°F)
                int sunnyTemp = IDEAL_TEMP_UPPER + 1 + random.nextInt(10); // 76 to 85
                garden.temperature(sunnyTemp);
                String message = "It's a sunny day! Temperature rose to " + sunnyTemp + "°F, plants are drying faster.";
                statusText.setText(message);
                GardenLogger.event(message);
                // Cancel any pending or active insulation cover
                if (insulationCoverActive || insulationPending) {
                    insulationCoverActive = false;
                    insulationPending = false;
                    insulationCyclesLeft = 0;
                    GardenLogger.event("Insulation cover canceled due to sunny day.");
                }
                // Display sun icon
                eventImageView.setImage(sunEventImage);
                eventImageView.setVisible(true);

                // Play sunshine animation on all plants
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                            final int row = r;
                            final int col = c;
                            // Small delay between animations for visual effect
                            final int delay = random.nextInt(300);
                            Timeline timeline = new Timeline(new KeyFrame(
                                    Duration.millis(delay),
                                    e -> {
                                        final StackPane cell = getNodeByRowColumnIndex(row, col);
                                        Platform.runLater(() -> AnimationFactory.playAnimation(cell,
                                                AnimationFactory.AnimationType.SUNSHINE));
                                    }));
                            timeline.play();
                        }
                    }
                }
                break;
            case 5: // Chilly day - temperature drop and insulation cover
                // Drop temperature below ideal range (e.g., 55-64°F)
                int coldTemp = IDEAL_TEMP_LOWER - 1 - random.nextInt(10); // 55 to 64
                garden.temperature(coldTemp);
                // Schedule insulation cover activation next cycle
                insulationPending = true;
                insulationCyclesLeft = 6;
                String chillMsg = "Chilly day! Temp dropped to " + coldTemp + "°F. " +
                        "Insulation cover will engage next cycle (" + insulationCyclesLeft + " cycles total).";
                statusText.setText(chillMsg);
                GardenLogger.event(chillMsg);
                // Display frost icon
                eventImageView.setImage(frostEventImage);
                eventImageView.setVisible(true);

                // Play frost animation on all plants
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                            final int row = r;
                            final int col = c;
                            // Small delay between animations for visual effect
                            final int delay = random.nextInt(300);
                            Timeline timeline = new Timeline(new KeyFrame(
                                    Duration.millis(delay),
                                    e -> {
                                        final StackPane cell = getNodeByRowColumnIndex(row, col);
                                        Platform.runLater(() -> AnimationFactory.playAnimation(cell,
                                                AnimationFactory.AnimationType.FROST));
                                    }));
                            timeline.play();
                        }
                    }
                }
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
                                AnimationFactory.playAnimation(cell, AnimationFactory.AnimationType.RAIN);
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
                Map<String, Integer> infestSummary = new HashMap<>();
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        // 10% chance per plant
                        if (!(plant instanceof NoPlant) && random.nextInt(10) == 0) {
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
                // Schedule pest spray next cycle
                sprayPending = true;
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
                // Restore temperature to ideal midpoint
                int idealTemp = (IDEAL_TEMP_LOWER + IDEAL_TEMP_UPPER) / 2;
                garden.temperature(idealTemp);
                String growthMessage = "Perfect 🌸growing conditions today! " + healthyPlantCount
                        + " plants are thriving, and temperature restored to " + idealTemp + "°F.";
                statusText.setText(growthMessage);
                GardenLogger.info(growthMessage);

                // Play sunshine animation on all plants
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        Plant plant = garden.getPlant(r, c);
                        if (!(plant instanceof NoPlant) && plant.getHealth() > 0) {
                            final int row = r;
                            final int col = c;
                            // Small delay between animations for visual effect
                            final int delay = random.nextInt(300);
                            Timeline timeline = new Timeline(new KeyFrame(
                                    Duration.millis(delay),
                                    e -> {
                                        final StackPane cell = getNodeByRowColumnIndex(row, col);
                                        Platform.runLater(() -> AnimationFactory.playAnimation(cell,
                                                AnimationFactory.AnimationType.SUNSHINE));
                                    }));
                            timeline.play();
                        }
                    }
                }
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

                            final int row = r;
                            final int col = c;
                            // Small delay between animations
                            final int delay = random.nextInt(300);
                            Timeline timeline = new Timeline(new KeyFrame(
                                    Duration.millis(delay),
                                    e -> {
                                        final StackPane cell = getNodeByRowColumnIndex(row, col);
                                        Platform.runLater(() -> AnimationFactory.playAnimation(cell,
                                                AnimationFactory.AnimationType.FARMER));
                                    }));
                            timeline.play();
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
        statsPanel.setMinWidth(200);
        statsPanel.setPrefWidth(250);
        statsPanel.setMaxWidth(280);

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
        // Add temperature indicator
        temperatureText.setText("Temperature: 0°F");
        temperatureText.setFill(Color.DARKBLUE);
        temperatureText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        // Add time display with clock and calendar icons
        // Don't set text directly as it will be bound to timer properties
        gameTimeText.setFill(Color.PURPLE);
        gameTimeText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        // Don't set text directly as it will be bound to timer properties
        sessionTimeText.setFill(Color.DARKSLATEGRAY);
        sessionTimeText.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        statsPanel.getChildren().addAll(
                livePlantsText,
                deadPlantsText,
                emptyPlotsText,
                plantedText,
                wateredText,
                temperatureText);

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
        rightPanels.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
        rightPanels.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        rightPanels.getChildren().addAll(statsPanel, logPanel);

        // Set VBox to be responsive
        VBox.setVgrow(statsPanel, Priority.NEVER); // Fixed size for stats
        VBox.setVgrow(logPanel, Priority.ALWAYS); // Log panel can grow

        // Create a scroll pane to allow scrolling if window gets too small
        ScrollPane scrollPane = new ScrollPane(rightPanels);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Find the root BorderPane and set the right side
        BorderPane borderPane = (BorderPane) gardenGrid.getScene().getRoot();
        borderPane.setRight(scrollPane);

        // Set up initial panel sizes
        Platform.runLater(() -> {
            if (gardenGrid.getScene() != null) {
                adjustPanelSizes(gardenGrid.getScene().getWidth());
            }
        });
    }

    /**
     * Adjust panel sizes based on window width
     */
    private void adjustPanelSizes(double windowWidth) {
        // Adjust panel width based on window width
        double panelWidth = Math.min(280, Math.max(200, windowWidth * 0.25));

        statsPanel.setPrefWidth(panelWidth);
        logPanel.setPrefWidth(panelWidth);
    }

    /**
     * Set up the log panel to display garden operation logs
     */
    private void setupLogPanel() {
        // Create log panel with improved styling
        logPanel = new VBox(8);
        logPanel.setStyle(
                "-fx-padding: 15px; -fx-background-color: #fff8f0; -fx-border-color: #ffcc80; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        logPanel.setMinWidth(200);
        logPanel.setPrefWidth(250);
        logPanel.setMaxWidth(280);
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
        logListView.setPrefHeight(180); // Adjust height to fit better
        logListView.setMinHeight(100);
        logListView.setStyle(
                "-fx-background-color: #fffaf0; -fx-background-radius: 5px; -fx-border-color: #ffe0b2; -fx-border-radius: 5px;");
        VBox.setVgrow(logListView, Priority.ALWAYS);

        // Create styled clear logs button
        clearLogsButton = new Button("🗑️ Clear Logs");
        clearLogsButton.setStyle("-fx-base: #ffcc80; -fx-font-weight: bold;");
        clearLogsButton.setOnAction(_ -> {
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
        statsUpdateTimer = new Timeline(new KeyFrame(Duration.seconds(2), _ -> updateStats()));
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
        // Update temperature display
        temperatureText.setText("Temperature: " + garden.getCurrentTemperature() + "°F");
    }

    /**
     * Sets up a timer to log total waterings in batch every 10 seconds.
     */
    private void setupWaterBatchLogTimer() {
        waterLogTimer = new Timeline(new KeyFrame(Duration.seconds(10), _ -> {
            if (waterBatchCount > 0) {
                GardenLogger.info("Watered " + waterBatchCount + " plants in the last 10 seconds");
                waterBatchCount = 0;
            }
        }));
        waterLogTimer.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Sets up a timer to log the garden state summary every real-time hour.
     */
    private void setupHourlyReportTimer() {
        hourlyReportTimer = new Timeline(new KeyFrame(Duration.seconds(3600), _ -> simApi.getState()));
        hourlyReportTimer.setCycleCount(Timeline.INDEFINITE);
        hourlyReportTimer.play();
    }

    /**
     * Clean up resources when the application is closing
     * Should be called when the application is about to close
     */
    public void cleanup() {
        try {
            // Shutdown the thread pool gracefully
            gardenExecutor.shutdown();
            GardenLogger.info("Garden application thread pool shutdown initiated");

            // Allow time for tasks to complete
            if (!gardenExecutor.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS)) {
                gardenExecutor.shutdownNow();
                GardenLogger.warning("Garden application forced thread pool shutdown");
            }
        } catch (InterruptedException ex) {
            gardenExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            GardenLogger.error("Garden application cleanup interrupted: " + ex.getMessage());
        } catch (Exception ex) {
            GardenLogger.error("Error during garden application cleanup: " + ex.getMessage());
        }
    }

    private void setupComboBoxCellFactory() {
        plantTypeComboBox.setCellFactory(_ -> new javafx.scene.control.ListCell<>() {
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

                        // Plant will never be null here since we assign a NoPlant as default
                        String imagePath = "assests/Tiles/" + plant.getImageUrl();
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        imageView.setImage(image);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        setText(item);
                        setGraphic(imageView);
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

                        // Plant will never be null here since we assign a NoPlant as default
                        String imagePath = "assests/Tiles/" + plant.getImageUrl();
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        imageView.setImage(image);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        setText(item);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setText(item);
                        setGraphic(null);
                    }
                }
            }
        });
    }
}
