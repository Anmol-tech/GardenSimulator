module com.example.project_csen_275 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project_csen_275 to javafx.fxml;
    exports com.example.project_csen_275;
}