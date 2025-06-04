module com.bomberman.model.bm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    // Exporter tous les packages nécessaires
    exports com.bomberman.main;
    exports com.bomberman.ui;
    exports com.bomberman.model;
    exports com.bomberman.game;

    // Ouvrir les packages pour JavaFX FXML
    opens com.bomberman.ui to javafx.fxml;
    opens com.bomberman.main to javafx.fxml;
}