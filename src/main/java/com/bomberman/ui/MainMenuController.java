package com.bomberman.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private void jouer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Bomberman");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherScores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scores.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("High Scores");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void quitter() {
        System.exit(0);
    }
}

