module com.example.project_csen_275 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.project_csen_275 to javafx.fxml;
    opens com.example.project_csen_275.Models to javafx.fxml;
    opens com.example.project_csen_275.Models.Plants to javafx.fxml;

    exports com.example.project_csen_275;
    exports com.example.project_csen_275.Models;
    exports com.example.project_csen_275.Models.Plants;
}