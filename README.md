# Garden Simulator 🌱

A comprehensive JavaFX-based garden simulation application that provides an interactive, real-time virtual garden experience. Built with modular Java architecture and featuring automated systems, environmental events, and visual animations.

## Features ✨

### Core Functionality

- **Interactive 5×5 Garden Grid**: Plant, water, and manage crops in real-time
- **Multiple Plant Types**: Carrot, Cherry, Corn, Pumpkin, and Sunflower varieties
- **Real-time Statistics**: Track living/dead plants, waterings, temperature, and session time
- **Visual Health Indicators**: Color-coded health and moisture levels for each plant

### Environmental System

- **Dynamic Weather Events**: Sunny days, rainy days, chilly weather with visual effects
- **Temperature Regulation**: Automatic insulation cover deployment during cold spells
- **Pest Management**: Realistic pest infestations with automated spray defense system
- **Special Events**: Perfect growth conditions and gardener visits

### Automation Features

- **Background Processing**: Multi-threaded updates via JavaFX Timeline and ThreadPoolExecutor
- **Smart Automation**: Automated watering, pest control, and environmental responses
- **Event Scheduling**: Random environmental events every 15 seconds during automation
- **Batch Logging**: Efficient logging system with 10-second batch updates

### User Interface

- **Responsive Design**: Scalable panels and grid layout
- **Visual Animations**: Water drops, sunshine, frost, and pest spray effects
- **Comprehensive Help**: Built-in documentation with gardening tips and controls
- **Activity Logging**: Real-time log viewer with clear/filter options

## Technology Stack 🛠️

- **Java 24**: Modern Java with advanced features
- **JavaFX 24.0.1**: Rich UI framework with FXML
- **Maven**: Dependency management and build automation
- **Modular Architecture**: Java Platform Module System (JPMS)
- **Concurrent Programming**: ExecutorService and Timeline for smooth performance

## Prerequisites 📋

- Java 24 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Operating System: Windows, macOS, or Linux

## Installation & Setup 🚀

### Clone the Repository

```bash
git clone https://github.com/Anmol-tech/Project_CSEN_275.git
cd Project_CSEN_275
```

### Build and Run

Using Maven wrapper (recommended):

```bash
# Windows
./mvnw clean javafx:run

# macOS/Linux
./mvnw clean javafx:run
```

Using system Maven:

```bash
mvn clean javafx:run
```

### Alternative Build Methods

```bash
# Compile only
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw clean package
```

## Usage Guide 🎮

### Basic Controls

- **Left Click**: Select a grid cell for planting
- **Right Click**: Water the plant in the selected cell
- **Plant Selection**: Choose plant type from dropdown, then click "Plant Selected"
- **Water All**: Instantly water all living plants in the garden
- **Update Garden**: Manually trigger a garden state update

### Automation Mode

1. Click **"Start Automation"** to enable automatic garden management
2. The system will:
   - Update garden state every 3 seconds
   - Randomly water plants every 18 seconds
   - Deploy pest spray when infestations occur
   - Trigger environmental events every 15 seconds
3. Click **"Stop Automation"** to return to manual control

### Environmental Events

- **Sunny Day**: Increases temperature, causes extra plant drying
- **Rainy Day**: Waters all plants automatically
- **Chilly Day**: Drops temperature, triggers insulation cover
- **Pest Infestation**: Adds pests to vulnerable plants
- **Perfect Growth**: Optimal conditions with extra watering
- **Gardener Visit**: Removes all pests and waters plants

### Temperature System

- **Ideal Range**: 65-75°F
- **Cold Effects**: Plants lose health below 65°F
- **Hot Effects**: Accelerated moisture loss above 75°F
- **Insulation Cover**: Automatically deployed during cold spells

## Project Structure 📁

```
src/main/java/com/example/project_csen_275/
├── GardenApp.java                 # Application entry point
├── GardenControllerFX.java        # Main UI controller
├── GardenSimulationAPI.java       # Core simulation logic
├── GardenLogger.java              # Logging system
├── GardenTimer.java               # Game time management
├── GardenDocumentation.java       # Help system
├── PlantSelector.java             # Plant type selector
├── Models/
│   ├── Garden.java                # Garden state management
│   └── Plants/
│       ├── Plant.java             # Base plant class
│       ├── Carrot.java            # Carrot plant implementation
│       ├── Cherry.java            # Cherry plant implementation
│       ├── Corn.java              # Corn plant implementation
│       ├── Pumpkin.java           # Pumpkin plant implementation
│       ├── Sunflower.java         # Sunflower plant implementation
│       └── NoPlant.java           # Empty soil representation
└── animations/
    └── AnimationFactory.java      # Visual effects system

src/main/resources/
├── garden-view.fxml               # UI layout definition
├── garden_config.csv             # Plant configuration data
└── assests/
    ├── Animation/                 # Animation sprites
    └── Tiles/                     # Plant and UI images
```

## Configuration 🔧

### Plant Properties

Plant characteristics are defined in `garden_config.csv`:

- Growth rates and health values
- Moisture requirements
- Pest vulnerabilities
- Visual appearance settings

### System Settings

Key constants in `GardenControllerFX.java`:

- `AUTO_UPDATE_INTERVAL`: Automation cycle time (3 seconds)
- `IDEAL_TEMP_LOWER/UPPER`: Temperature comfort zone (65-75°F)
- `SPRAY_INITIAL_DMG/SPRAY_SUBSEQUENT_DMG`: Pest spray effectiveness

## Logging 📝

The application maintains comprehensive logs:

- **Garden Events**: Plant actions, environmental changes
- **System Status**: Automation state, errors, warnings
- **Performance**: Background task execution times
- **User Actions**: Manual interventions and selections

Logs are stored in the `logs/` directory with daily rotation.

## Architecture Design 🏗️

### Design Patterns

- **MVC Pattern**: Separation of UI, logic, and data
- **Factory Pattern**: Animation and plant creation
- **Observer Pattern**: Real-time UI updates
- **Command Pattern**: Event handling system

### Concurrency Model

- **JavaFX Application Thread**: UI updates and user interactions
- **Background Thread Pool**: Garden state calculations
- **Timeline Schedulers**: Periodic automation tasks
- **Platform.runLater()**: Thread-safe UI updates

### Module System

The application uses Java's module system for:

- Clear dependency boundaries
- Improved security and performance
- Better maintainability

## Development 👨‍💻

### Adding New Plants

1. Create new class extending `Plant` in `Models/Plants/`
2. Add plant images to `src/main/resources/assests/Tiles/`
3. Update `PlantSelector.java` with new plant type
4. Add plant data to `garden_config.csv`

### Adding New Events

1. Extend the `triggerRandomEvent()` method in `GardenControllerFX.java`
2. Add new event type to `eventComboBox` initialization
3. Create corresponding animation in `AnimationFactory.java`

### Testing

```bash
# Run unit tests
./mvnw test

# Run with debugging
./mvnw clean javafx:run -Djavafx.args="--debug"
```

## Troubleshooting 🔧

### Common Issues

1. **JavaFX Runtime Not Found**

   - Ensure Java 24+ with JavaFX modules is installed
   - Use the provided Maven wrapper which handles JavaFX dependencies

2. **Performance Issues**

   - Reduce automation frequency by increasing `AUTO_UPDATE_INTERVAL`
   - Lower animation quality in resource-constrained environments

3. **UI Scaling Problems**
   - Adjust system DPI settings
   - Modify CSS scaling factors in FXML files

### Support

- Check the built-in help system (Help button in application)
- Review log files in `logs/` directory for error details
- Ensure all dependencies are properly installed

## Contributing 🤝

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License 📄

This project is developed as part of CSEN 275 coursework. Please refer to your institution's academic integrity policies.

## Acknowledgments 🙏

- JavaFX community for excellent documentation and examples
- Plant imagery and animations from open-source resources
- Course instructors and peers for feedback and testing

---

**Happy Gardening! 🌻**
