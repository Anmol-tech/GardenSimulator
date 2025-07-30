package com.example.project_csen_275;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Comprehensive documentation system for the Garden Simulator application.
 * Provides detailed help guides, log file viewer, and system information.
 * 
 * @version 1.0
 * @since July 29, 2025
 */
public class GardenDocumentation {

    private static final String STYLE_SECTION_TITLE = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1b5e20; -fx-padding: 5 0 5 0;";
    private static final String STYLE_CONTENT = "-fx-background-color: rgba(255,255,255,0.7); -fx-font-size: 13px; -fx-text-fill: #333333; "
            +
            "-fx-padding: 10px; -fx-background-radius: 5; -fx-border-color: rgba(200,230,200,0.8); " +
            "-fx-border-radius: 5; -fx-border-width: 1;";
    private static final String STYLE_TAB_CONTENT = "-fx-background-color: linear-gradient(#f8fff8, #e0f2e0);";

    private Stage stage;
    private TabPane tabPane;

    /**
     * Create and display the documentation window
     * 
     * @param owner The owner window
     */
    public void showDocumentation(Stage owner) {
        // Create main window
        stage = new Stage();
        stage.setTitle("Garden Simulator Documentation");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);

        // Create tab pane for different documentation sections
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create tabs
        Tab userGuideTab = createUserGuideTab();
        Tab plantCareTab = createPlantCareTab();
        Tab logHelperTab = createLogHelperTab();
        Tab logViewerTab = createLogViewerTab();
        Tab systemInfoTab = createSystemInfoTab();

        tabPane.getTabs().addAll(userGuideTab, plantCareTab, logHelperTab, logViewerTab, systemInfoTab);

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(tabPane);

        // Close button at bottom
        Button closeButton = new Button("Close Documentation");
        closeButton.setStyle("-fx-base: #388e3c; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> stage.close());

        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        bottomBox.getChildren().add(closeButton);
        mainLayout.setBottom(bottomBox);

        Scene scene = new Scene(mainLayout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Create the User Guide tab with comprehensive instructions
     * 
     * @return Tab containing user guide
     */
    private Tab createUserGuideTab() {
        Tab tab = new Tab("User Guide");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle(STYLE_TAB_CONTENT);

        // Title
        Text title = new Text("Garden Simulator User Guide");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setUnderline(true);

        content.getChildren().addAll(
                createCenteredTitle(title),
                createSeparator(),

                // Introduction
                createSection("Introduction",
                        "Garden Simulator is an interactive application that allows you to create and manage your own virtual garden. "
                                +
                                "You can plant different types of plants, water them, monitor their health, and deal with various events "
                                +
                                "and challenges such as pests, weather changes, and temperature fluctuations.\n\n" +
                                "This guide provides comprehensive instructions on how to use all features of the Garden Simulator application."),

                // Getting Started
                createSection("Getting Started",
                        "1. The main interface displays a 5x5 grid representing your garden plots\n" +
                                "2. The right panel shows garden statistics and an activity log\n" +
                                "3. The bottom panel contains controls for plant selection, events, and automation\n" +
                                "4. The top-right corner displays the game time and session duration\n\n" +
                                "To begin gardening:\n" +
                                "• Click on any empty grid cell to select it\n" +
                                "• Choose a plant type from the dropdown menu in the bottom panel\n" +
                                "• Click 'Plant Selected' to place your chosen plant in the selected cell\n" +
                                "• Water your plants regularly by right-clicking on them or using the 'Water All' button"),

                // Core Features
                createSection("Core Features",
                        "• Plant Selection and Placement\n" +
                                "  - Left-click a grid cell to select it for planting\n" +
                                "  - Choose a plant type from the dropdown menu\n" +
                                "  - Click 'Plant Selected' to place the plant\n" +
                                "  - To remove a plant, select 'Empty' from the dropdown and place it\n\n" +

                                "• Plant Care and Watering\n" +
                                "  - Right-click on individual plants to water them\n" +
                                "  - Use the 'Water All' button to water every plant at once\n" +
                                "  - Monitor plant health (♥) and moisture levels (💧) displayed on each plant\n" +
                                "  - Plants with higher moisture levels maintain better health\n\n" +

                                "• Garden Updates\n" +
                                "  - Use the 'Update Garden' button to advance the garden simulation by one cycle\n" +
                                "  - Each update changes moisture levels, grows plants, and applies environmental effects\n"
                                +
                                "  - Track changes in the activity log on the right side panel\n\n" +

                                "• Pest Management\n" +
                                "  - Pests appear naturally or can be manually added with 'Add Pests' button\n" +
                                "  - Infested plants show a bug icon (🐛) instead of the health icon (♥)\n" +
                                "  - Use 'Remove Bugs' button to eliminate all pests immediately\n" +
                                "  - Pest spray automatically activates to gradually reduce pest health\n\n" +

                                "• Automation System\n" +
                                "  - Click 'Start Automation' to let the garden run automatically\n" +
                                "  - The simulation will update every 3 seconds\n" +
                                "  - Plants receive random watering and care\n" +
                                "  - Random events will occur to make gardening interesting\n" +
                                "  - Click 'Stop Automation' to regain manual control"),

                // Weather & Events
                createSection("Weather and Events",
                        "The garden simulation includes various weather events that affect your plants:\n\n" +
                                "• Sunny Day\n" +
                                "  - Increases temperature above 75°F\n" +
                                "  - Accelerates moisture loss in plants\n" +
                                "  - Can cause heat stress (-8 health per cycle) if too hot\n\n" +

                                "• Rainy Day\n" +
                                "  - Waters all plants automatically\n" +
                                "  - Increases plant health\n" +
                                "  - Restores optimal moisture levels\n\n" +

                                "• Chilly Day\n" +
                                "  - Drops temperature below 65°F\n" +
                                "  - Causes cold damage (-2 health per cycle)\n" +
                                "  - Activates insulation cover to gradually restore temperature\n\n" +

                                "• Pest Infestation\n" +
                                "  - Adds pests to random plants\n" +
                                "  - Reduces plant health until pests are eliminated\n" +
                                "  - Triggers automatic pest spray defense\n\n" +

                                "• Perfect Growth\n" +
                                "  - Optimal conditions for all plants\n" +
                                "  - Doubles water effectiveness\n" +
                                "  - Restores ideal temperature\n\n" +

                                "• Gardener Visit\n" +
                                "  - Removes all pests\n" +
                                "  - Waters all plants\n" +
                                "  - Improves plant health"),

                // User Interface Elements
                createSection("User Interface Guide",
                        "• Top Panel\n" +
                                "  - Application title\n" +
                                "  - Status message showing latest activity\n" +
                                "  - Game time display (top-right corner)\n\n" +

                                "• Garden Grid (Center)\n" +
                                "  - 5x5 grid of plant cells\n" +
                                "  - Each cell shows plant image, health, and moisture level\n" +
                                "  - Selected cells have blue borders\n" +
                                "  - Plants with pests display a bug icon and pest health\n\n" +

                                "• Control Panel (Bottom)\n" +
                                "  - Plant selection dropdown and button\n" +
                                "  - Event trigger controls\n" +
                                "  - Update Garden, Water All, Pest management buttons\n" +
                                "  - Help button\n" +
                                "  - Automation toggle\n\n" +

                                "• Statistics Panel (Right)\n" +
                                "  - Plant counts (living, dead, empty)\n" +
                                "  - Water level average\n" +
                                "  - Health level average\n" +
                                "  - Current garden temperature\n\n" +

                                "• Activity Log (Right, Below Stats)\n" +
                                "  - Real-time updates of all garden activities\n" +
                                "  - Color-coded messages (green for info, yellow for warnings, red for errors)\n" +
                                "  - Chronological record of all garden events"),

                // Weather & Events
                createSection("Weather and Events",
                        "The garden simulation includes various weather events that affect your plants:\n\n" +
                                "• Sunny Day\n" +
                                "  - Increases temperature above 75°F\n" +
                                "  - Accelerates moisture loss in plants\n" +
                                "  - Can cause heat stress (-8 health per cycle) if too hot\n\n" +

                                "• Rainy Day\n" +
                                "  - Waters all plants automatically\n" +
                                "  - Increases plant health\n" +
                                "  - Restores optimal moisture levels\n\n" +

                                "• Chilly Day\n" +
                                "  - Drops temperature below 65°F\n" +
                                "  - Causes cold damage (-2 health per cycle)\n" +
                                "  - Activates insulation cover to gradually restore temperature\n\n" +

                                "• Pest Infestation\n" +
                                "  - Adds pests to random plants\n" +
                                "  - Reduces plant health until pests are eliminated\n" +
                                "  - Triggers automatic pest spray defense\n\n" +

                                "• Perfect Growth\n" +
                                "  - Optimal conditions for all plants\n" +
                                "  - Doubles water effectiveness\n" +
                                "  - Restores ideal temperature\n\n" +

                                "• Gardener Visit\n" +
                                "  - Removes all pests\n" +
                                "  - Waters all plants\n" +
                                "  - Improves plant health"));

        scrollPane.setContent(content);
        tab.setContent(scrollPane);
        return tab;
    }

    /**
     * Create the Plant Care Guide tab with detailed plant information
     * 
     * @return Tab containing plant care information
     */
    private Tab createPlantCareTab() {
        Tab tab = new Tab("Plant Care Guide");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle(STYLE_TAB_CONTENT);

        // Title
        Text title = new Text("Plant Care Guide");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setUnderline(true);

        content.getChildren().addAll(
                createCenteredTitle(title),
                createSeparator(),

                // Introduction
                createSection("Plant Types and Characteristics",
                        "The Garden Simulator features several plant types, each with unique characteristics, " +
                                "growth patterns, and care requirements. Understanding each plant's needs will help you "
                                +
                                "maintain a thriving garden."),

                // Carrot
                createPlantSection(
                        "🥕 Carrot (Root Vegetable)",
                        "Growth Rate: Fast\n" +
                                "Water Needs: Moderate\n" +
                                "Temperature Tolerance: High\n" +
                                "Pest Vulnerability: Medium",

                        "Carrots are fast-growing root vegetables that mature quickly in your garden. They require " +
                                "moderate amounts of water and can tolerate both warm and cool temperatures relatively well. "
                                +
                                "Their highest vulnerability is to root-eating pests.\n\n" +

                                "Care Tips:\n" +
                                "• Water when moisture drops below 50%\n" +
                                "• Perform best in temperatures between 60-75°F\n" +
                                "• Monitor for pests regularly"),

                // Sunflower
                createPlantSection(
                        "🌻 Sunflower (Ornamental)",
                        "Growth Rate: Medium\n" +
                                "Water Needs: High\n" +
                                "Temperature Tolerance: Medium-High\n" +
                                "Pest Vulnerability: Medium",

                        "Sunflowers are beautiful ornamental plants that add visual appeal to your garden. They require "
                                +
                                "significant amounts of water and prefer warm temperatures. They're moderately vulnerable to certain pests.\n\n"
                                +

                                "Care Tips:\n" +
                                "• Keep moisture levels above 60% for optimal growth\n" +
                                "• Thrive in temperatures between 65-85°F\n" +
                                "• May need more frequent watering during Sunny Day events"),

                // Cherry
                createPlantSection(
                        "🍒 Cherry (Fruit Tree)",
                        "Growth Rate: Slow\n" +
                                "Water Needs: Medium\n" +
                                "Temperature Tolerance: Medium\n" +
                                "Pest Vulnerability: High",

                        "Cherry trees are slow-growing fruit trees that take time to mature but provide valuable fruit. "
                                +
                                "They have moderate water requirements and are somewhat sensitive to temperature extremes. They are "
                                +
                                "highly attractive to pests.\n\n" +

                                "Care Tips:\n" +
                                "• Maintain consistent moisture levels around 50-60%\n" +
                                "• Prefer temperatures between 60-75°F\n" +
                                "• Watch closely for pest infestations and treat promptly\n" +
                                "• Most rewarding when fully grown"),

                // Corn
                createPlantSection(
                        "🌽 Corn (Grain Crop)",
                        "Growth Rate: Medium-Fast\n" +
                                "Water Needs: Very High\n" +
                                "Temperature Tolerance: Medium\n" +
                                "Pest Vulnerability: High",

                        "Corn is a staple grain crop that grows at a moderate-to-fast rate. It's extremely thirsty and requires "
                                +
                                "frequent watering. It has moderate temperature tolerance but is quite vulnerable to certain pests.\n\n"
                                +

                                "Care Tips:\n" +
                                "• Keep moisture levels above 70% for optimal growth\n" +
                                "• Performs best in temperatures between 65-80°F\n" +
                                "• Monitor closely during drought or Sunny Day events\n" +
                                "• Use pest control measures proactively"),

                // Pumpkin
                createPlantSection(
                        "🎃 Pumpkin (Vine Crop)",
                        "Growth Rate: Medium\n" +
                                "Water Needs: Medium\n" +
                                "Temperature Tolerance: Medium\n" +
                                "Pest Vulnerability: Medium",

                        "Pumpkins are sprawling vine plants that grow at a moderate rate. They have balanced water requirements "
                                +
                                "and moderate tolerance for temperature variations. They have average vulnerability to pests.\n\n"
                                +

                                "Care Tips:\n" +
                                "• Maintain moisture levels around 50-60%\n" +
                                "• Thrive in temperatures between 65-75°F\n" +
                                "• All-around balanced plant suitable for most gardeners"),

                // General Plant Care
                createSection("General Plant Care Guidelines",
                        "• Watering Strategy\n" +
                                "  - Use 'Water All' for efficient garden-wide watering\n" +
                                "  - Water more frequently during hot weather and sunny days\n" +
                                "  - Each watering restores moisture and provides up to +9 health\n\n" +

                                "• Temperature Management\n" +
                                "  - Monitor garden temperature in the stats panel\n" +
                                "  - Ideal temperature range is 65-75°F for most plants\n" +
                                "  - Below 65°F causes cold damage (-2 health/cycle)\n" +
                                "  - Above 75°F causes heat stress (-8 health/cycle)\n\n" +

                                "• Pest Control\n" +
                                "  - Inspect plants regularly for pest indicators (🐛)\n" +
                                "  - Pests reduce plant health by 5 points per cycle\n" +
                                "  - Pest spray automatically deploys when pests are detected\n" +
                                "  - Use 'Remove Bugs' for immediate pest elimination\n" +
                                "  - Well-watered plants have better pest resistance\n\n" +

                                "• Health Management\n" +
                                "  - Keep plant health above 50 for optimal growth\n" +
                                "  - Health indicators change color based on status:\n" +
                                "    * Green: Good health (>75)\n" +
                                "    * Yellow: Medium health (50-75)\n" +
                                "    * Orange: Low health (1-50)\n" +
                                "    * Red: Pest infestation\n" +
                                "  - Plants die when health reaches zero"));

        scrollPane.setContent(content);
        tab.setContent(scrollPane);
        return tab;
    }

    /**
     * Create the Log Helper tab explaining the logging system
     * 
     * @return Tab containing log system documentation
     */
    private Tab createLogHelperTab() {
        Tab tab = new Tab("Log System Guide");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle(STYLE_TAB_CONTENT);

        // Title
        Text title = new Text("Garden Simulator Logging System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setUnderline(true);

        content.getChildren().addAll(
                createCenteredTitle(title),
                createSeparator(),

                // Introduction
                createSection("About the Logging System",
                        "The Garden Simulator includes a comprehensive logging system that records all activities, events, "
                                +
                                "and errors during your gardening session. Logs are displayed in real-time in the Activity Log panel "
                                +
                                "and are also saved to disk for future reference. Understanding the log format will help you track "
                                +
                                "the history of your garden and diagnose any issues."),

                // Log Format
                createSection("Log Entry Format",
                        "Each log entry follows this standard format:\n\n" +
                                "TIMESTAMP EMOJI[LEVEL] MESSAGE\n\n" +
                                "• TIMESTAMP: The date and time when the event occurred (HH:MM:SS format)\n" +
                                "• EMOJI: Visual indicator of the log type\n" +
                                "• LEVEL: The severity or category of the log entry\n" +
                                "• MESSAGE: Detailed description of the event\n\n" +
                                "Example: 14:32:15 ℹ️[INFO] Planted Carrot at position [2,3]"),

                // Log Levels
                createSection("Log Levels and Categories",
                        "The logging system uses several levels to categorize events:\n\n" +
                                "• ℹ️ INFO: General information about normal operations\n" +
                                "  - Plant placement, watering, garden updates\n" +
                                "  - Automation status changes\n" +
                                "  - Example: \"Planted Carrot at position [2,3]\"\n\n" +

                                "• ⚠️ WARNING: Important alerts that need attention but aren't errors\n" +
                                "  - Pest appearances\n" +
                                "  - Temperature extremes\n" +
                                "  - Low moisture levels\n" +
                                "  - Example: \"Temperature reached 82°F - plants may suffer heat stress!\"\n\n" +

                                "• ❌ ERROR: Problems that prevented an operation from completing\n" +
                                "  - Application errors\n" +
                                "  - File access issues\n" +
                                "  - Unexpected exceptions\n" +
                                "  - Example: \"Failed to load pest spray image: File not found\"\n\n" +

                                "• 🎮 EVENT: Special garden events and significant changes\n" +
                                "  - Weather events (Sunny Day, Rainy Day, etc.)\n" +
                                "  - Gardener visits\n" +
                                "  - Pest spray deployments\n" +
                                "  - Example: \"It's a rainy day! All plants received water.\""),

                // Log File Storage
                createSection("Log File Storage",
                        "Log files are saved in the 'logs' directory with the naming format:\n\n" +
                                "garden_log_YYYY-MM-DD.log\n\n" +
                                "• Each day gets a new log file\n" +
                                "• Files are plain text and can be opened with any text editor\n" +
                                "• Log entries are appended to the file in chronological order\n\n" +
                                "The Log Viewer tab in this documentation window allows you to browse and search through your log files."),

                // Using Logs Effectively
                createSection("Using Logs Effectively",
                        "The logging system can help you in several ways:\n\n" +
                                "• Tracking Plant History\n" +
                                "  - When plants were added to the garden\n" +
                                "  - Watering frequency and timing\n" +
                                "  - When and why plants died\n\n" +

                                "• Monitoring Environmental Conditions\n" +
                                "  - Temperature fluctuations\n" +
                                "  - Weather events\n" +
                                "  - Pest infestations\n\n" +

                                "• Diagnosing Issues\n" +
                                "  - Understanding plant health declines\n" +
                                "  - Identifying recurring problems\n" +
                                "  - Tracking application errors\n\n" +

                                "• Improving Your Gardening\n" +
                                "  - Analyzing patterns over time\n" +
                                "  - Understanding cause-effect relationships\n" +
                                "  - Refining your plant care strategy"));

        scrollPane.setContent(content);
        tab.setContent(scrollPane);
        return tab;
    }

    /**
     * Create the Log Viewer tab with interactive log file browser
     * 
     * @return Tab containing log viewer
     */
    private Tab createLogViewerTab() {
        Tab tab = new Tab("Log Viewer");

        BorderPane layout = new BorderPane();
        layout.setStyle(STYLE_TAB_CONTENT);

        // Title
        Label title = new Label("Garden Simulator Log Viewer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setPadding(new Insets(10));
        layout.setTop(title);

        // Main content
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Log file selector
        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER_LEFT);
        Label fileLabel = new Label("Select Log File:");
        fileLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Get log files
        List<String> logFiles = getLogFiles();
        javafx.scene.control.ComboBox<String> fileCombo = new javafx.scene.control.ComboBox<>();
        fileCombo.getItems().addAll(logFiles);
        if (!logFiles.isEmpty()) {
            fileCombo.setValue(logFiles.get(0));
        }
        fileCombo.setPrefWidth(300);

        Button loadButton = new Button("Load Log");
        loadButton.setStyle("-fx-base: #4CAF50;");

        fileBox.getChildren().addAll(fileLabel, fileCombo, loadButton);
        content.getChildren().add(fileBox);

        // Search box
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        Label searchLabel = new Label("Search Logs:");
        searchLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPrefWidth(300);
        searchField.setPromptText("Enter search term...");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-base: #2196F3;");

        searchBox.getChildren().addAll(searchLabel, searchField, searchButton);
        content.getChildren().add(searchBox);

        // Filter options
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = new Label("Filter by Level:");
        filterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        javafx.scene.control.CheckBox infoCheck = new javafx.scene.control.CheckBox("INFO");
        javafx.scene.control.CheckBox warnCheck = new javafx.scene.control.CheckBox("WARNING");
        javafx.scene.control.CheckBox errorCheck = new javafx.scene.control.CheckBox("ERROR");
        javafx.scene.control.CheckBox eventCheck = new javafx.scene.control.CheckBox("EVENT");

        // Select all by default
        infoCheck.setSelected(true);
        warnCheck.setSelected(true);
        errorCheck.setSelected(true);
        eventCheck.setSelected(true);

        Button applyFilterButton = new Button("Apply Filter");
        applyFilterButton.setStyle("-fx-base: #FF9800;");

        filterBox.getChildren().addAll(filterLabel, infoCheck, warnCheck, errorCheck, eventCheck, applyFilterButton);
        content.getChildren().add(filterBox);

        // Separator
        content.getChildren().add(new Separator());

        // Log display
        Label logContentLabel = new Label("Log Contents:");
        logContentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        content.getChildren().add(logContentLabel);

        TextArea logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setWrapText(true);
        logTextArea.setFont(Font.font("Monospaced", 12));
        logTextArea.setPrefHeight(300);
        logTextArea.setText("Select a log file and click 'Load Log' to view its contents.");
        content.getChildren().add(logTextArea);
        VBox.setVgrow(logTextArea, Priority.ALWAYS);

        // Set up log loading functionality
        loadButton.setOnAction(e -> {
            String selectedFile = fileCombo.getValue();
            if (selectedFile != null) {
                String logPath = "logs/" + selectedFile;
                try {
                    String logContent = readLogFile(logPath);
                    logTextArea.setText(logContent);
                } catch (IOException ex) {
                    logTextArea.setText("Error loading log file: " + ex.getMessage());
                }
            }
        });

        // Set up search functionality
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                // Reload the whole file
                loadButton.fire();
                return;
            }

            String currentContent = logTextArea.getText();
            StringBuilder searchResults = new StringBuilder();
            searchResults.append("Search results for: \"").append(searchTerm).append("\"\n\n");

            String[] lines = currentContent.split("\n");
            boolean found = false;

            for (String line : lines) {
                if (line.toLowerCase().contains(searchTerm.toLowerCase())) {
                    searchResults.append(line).append("\n");
                    found = true;
                }
            }

            if (!found) {
                searchResults.append("No matches found for: \"").append(searchTerm).append("\"");
            }

            logTextArea.setText(searchResults.toString());
        });

        // Set up filter functionality
        applyFilterButton.setOnAction(e -> {
            String selectedFile = fileCombo.getValue();
            if (selectedFile != null) {
                String logPath = "logs/" + selectedFile;
                try {
                    List<String> logLines = Files.readAllLines(Paths.get(logPath));
                    StringBuilder filteredContent = new StringBuilder();

                    for (String line : logLines) {
                        if ((infoCheck.isSelected() && line.contains("[INFO]")) ||
                                (warnCheck.isSelected() && line.contains("[WARNING]")) ||
                                (errorCheck.isSelected() && line.contains("[ERROR]")) ||
                                (eventCheck.isSelected() && line.contains("[EVENT]"))) {
                            filteredContent.append(line).append("\n");
                        }
                    }

                    logTextArea.setText(filteredContent.toString());
                } catch (IOException ex) {
                    logTextArea.setText("Error applying filter: " + ex.getMessage());
                }
            }
        });

        layout.setCenter(content);
        tab.setContent(layout);
        return tab;
    }

    /**
     * Create the System Info tab with technical details
     * 
     * @return Tab containing system information
     */
    private Tab createSystemInfoTab() {
        Tab tab = new Tab("System Info");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle(STYLE_TAB_CONTENT);

        // Title
        Text title = new Text("System Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setUnderline(true);

        content.getChildren().addAll(
                createCenteredTitle(title),
                createSeparator(),

                // Application Info
                createSection("Application Information",
                        "• Name: Garden Simulator\n" +
                                "• Version: 1.0\n" +
                                "• Release Date: July 29, 2025\n" +
                                "• Framework: JavaFX\n" +
                                "• Build System: Maven"),

                // Technical Architecture
                createSection("Technical Architecture",
                        "Garden Simulator is built using a modular architecture with the following components:\n\n" +

                                "• GardenApp.java\n" +
                                "  - Main application entry point\n" +
                                "  - Initializes JavaFX framework\n" +
                                "  - Loads primary FXML layout\n\n" +

                                "• GardenControllerFX.java\n" +
                                "  - Primary controller handling UI interactions\n" +
                                "  - Manages grid display, plant placement, and user controls\n" +
                                "  - Implements simulation update logic\n\n" +

                                "• Garden.java\n" +
                                "  - Core model representing the garden state\n" +
                                "  - Manages plant collection and garden properties\n" +
                                "  - Implements garden state update logic\n\n" +

                                "• GardenSimulationAPI.java\n" +
                                "  - High-level API providing garden operations\n" +
                                "  - Handles plant-specific behaviors and garden initialization\n\n" +

                                "• PlantSelector.java\n" +
                                "  - UI component for plant selection\n" +
                                "  - Manages plant type list and imagery\n\n" +

                                "• GardenLogger.java\n" +
                                "  - Logging system for application events\n" +
                                "  - Writes to console and persistent log files\n\n" +

                                "• Models/Plants/*\n" +
                                "  - Plant class hierarchy with specific plant implementations\n" +
                                "  - Defines unique properties and behaviors for each plant type"),

                // System Requirements
                createSection("System Requirements",
                        "• Operating Systems:\n" +
                                "  - Windows 10 or later\n" +
                                "  - macOS 10.14 or later\n" +
                                "  - Linux (with compatible graphics drivers)\n\n" +

                                "• Minimum Hardware:\n" +
                                "  - 2 GHz dual-core processor\n" +
                                "  - 4 GB RAM\n" +
                                "  - 200 MB available disk space\n" +
                                "  - Screen resolution: 1024x768 or higher\n\n" +

                                "• Software Requirements:\n" +
                                "  - Java Runtime Environment (JRE) 11 or later\n" +
                                "  - JavaFX 11 or later"),

                // Data Storage
                createSection("Data Storage",
                        "Garden Simulator stores data in the following locations:\n\n" +

                                "• Configuration Data\n" +
                                "  - File: garden_config.csv\n" +
                                "  - Location: src/main/resources/\n" +
                                "  - Purpose: Initial garden configuration settings\n\n" +

                                "• Log Files\n" +
                                "  - Files: garden_log_YYYY-MM-DD.log\n" +
                                "  - Location: logs/ directory\n" +
                                "  - Purpose: Activity and error logging\n\n" +

                                "• Asset Files\n" +
                                "  - Location: src/main/resources/com/example/project_csen_275/assests/\n" +
                                "  - Purpose: Plant imagery, animations, and UI elements"),

                // Troubleshooting
                createSection("Troubleshooting",
                        "If you encounter issues with Garden Simulator, try these troubleshooting steps:\n\n" +

                                "• Application Won't Start\n" +
                                "  - Verify Java installation (java -version)\n" +
                                "  - Check for adequate disk space\n" +
                                "  - Run with console to see error messages (./mvnw clean javafx:run)\n\n" +

                                "• Visual Glitches\n" +
                                "  - Update your graphics drivers\n" +
                                "  - Try a different screen resolution\n" +
                                "  - Verify JavaFX installation\n\n" +

                                "• Plants Not Responding\n" +
                                "  - Check activity log for errors\n" +
                                "  - Verify garden_config.csv is present and valid\n" +
                                "  - Restart the application\n\n" +

                                "• Missing Log Files\n" +
                                "  - Ensure the logs/ directory exists and is writable\n" +
                                "  - Check for disk space issues\n\n" +

                                "For persistent issues, check the error log files in the logs/ directory for detailed information."));

        scrollPane.setContent(content);
        tab.setContent(scrollPane);
        return tab;
    }

    /**
     * Create a centered title container
     * 
     * @param title The title text component
     * @return HBox containing the centered title
     */
    private HBox createCenteredTitle(Text title) {
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        return titleBox;
    }

    /**
     * Create a section with title and content
     * 
     * @param title   Section title
     * @param content Section content text
     * @return VBox containing the section
     */
    private VBox createSection(String title, String content) {
        VBox section = new VBox(5);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(STYLE_SECTION_TITLE);

        TextFlow contentFlow = new TextFlow();
        Text contentText = new Text(content);
        contentText.setWrappingWidth(750);
        contentFlow.getChildren().add(contentText);
        contentFlow.setStyle(STYLE_CONTENT);

        section.getChildren().addAll(titleLabel, contentFlow);
        return section;
    }

    /**
     * Create a detailed plant section with statistics and care info
     * 
     * @param name    Plant name with emoji
     * @param stats   Plant statistics summary
     * @param details Detailed plant information
     * @return VBox containing the plant section
     */
    private VBox createPlantSection(String name, String stats, String details) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(0, 0, 10, 0));

        // Plant name title
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        // Stats box
        TextArea statsArea = new TextArea(stats);
        statsArea.setEditable(false);
        statsArea.setPrefRowCount(4);
        statsArea.setPrefWidth(750);
        statsArea.setStyle("-fx-control-inner-background: #f0f7e6; -fx-font-family: monospace; " +
                "-fx-font-size: 13px; -fx-font-weight: bold;");

        // Details
        TextFlow detailsFlow = new TextFlow();
        Text detailsText = new Text(details);
        detailsText.setWrappingWidth(750);
        detailsFlow.getChildren().add(detailsText);
        detailsFlow.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-font-size: 13px; " +
                "-fx-text-fill: #333333; -fx-padding: 10px; -fx-background-radius: 5; " +
                "-fx-border-color: rgba(200,230,200,0.8); -fx-border-radius: 5; -fx-border-width: 1;");

        section.getChildren().addAll(nameLabel, statsArea, detailsFlow);
        return section;
    }

    /**
     * Create a visual separator
     * 
     * @return Styled separator
     */
    private Separator createSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #81c784;");
        return sep;
    }

    /**
     * Get list of available log files
     * 
     * @return List of log file names
     */
    private List<String> getLogFiles() {
        List<String> logFiles = new ArrayList<>();
        File logsDir = new File("logs");

        if (logsDir.exists() && logsDir.isDirectory()) {
            File[] files = logsDir.listFiles((dir, name) -> name.startsWith("garden_log_") && name.endsWith(".log"));
            if (files != null) {
                for (File file : files) {
                    logFiles.add(file.getName());
                }
            }
        }

        return logFiles;
    }

    /**
     * Read the contents of a log file
     * 
     * @param path Path to the log file
     * @return String containing the file contents
     * @throws IOException if file can't be read
     */
    private String readLogFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
