package com.example.project_csen_275;

import com.example.project_csen_275.Models.Plants.*;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.HashMap;
import java.util.Map;

public class PlantSelector {

    private final ComboBox<String> plantSelector;
    private final Map<String, Class<? extends Plant>> plantTypes;

    public PlantSelector() {
        plantSelector = new ComboBox<>();
        plantTypes = new HashMap<>();

        // Initialize plant types
        plantTypes.put("Empty", NoPlant.class);
        plantTypes.put("Carrot", Carrot.class);
        plantTypes.put("Cherry", Cherry.class);
        plantTypes.put("Corn", Corn.class);
        plantTypes.put("Pumpkin", Pumpkin.class);
        plantTypes.put("Sunflower", Sunflower.class);

        // Add plant types to combo box
        plantSelector.getItems().addAll(plantTypes.keySet());

        // Set default selection
        plantSelector.setValue("Empty");

        // Set cell factory to show plant images in dropdown
        plantSelector.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        Plant plant = plantTypes.get(item).getDeclaredConstructor().newInstance();
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

    public ComboBox<String> getComboBox() {
        return plantSelector;
    }

    public Plant createSelectedPlant() {
        try {
            String selectedType = plantSelector.getValue();
            return plantTypes.get(selectedType).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new NoPlant();
        }
    }

    public Map<String, Class<? extends Plant>> getPlantTypes() {
        return plantTypes;
    }

    public Plant createPlantByName(String plantName) {
        try {
            return plantTypes.get(plantName).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new NoPlant();
        }
    }
}
