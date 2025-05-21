module com.gameproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gameproject to javafx.fxml;
    exports com.gameproject;
}
