package com.bomberman.ui;

import com.bomberman.game.ScoreManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ScoresController implements Initializable {
    @FXML private ListView<String> scoresListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ScoreManager scoreManager = new ScoreManager();
        var scores = scoreManager.getTopScores();

        var items = FXCollections.<String>observableArrayList();
        for (int i = 0; i < scores.size(); i++) {
            var score = scores.get(i);
            items.add((i + 1) + ". " + score.getNom() + " - " + score.getScore() + " points");
        }

        if (items.isEmpty()) {
            items.add("Aucun score enregistré");
        }

        scoresListView.setItems(items);
    }

    @FXML
    private void fermer() {
        Stage stage = (Stage) scoresListView.getScene().getWindow();
        stage.close();
    }
}