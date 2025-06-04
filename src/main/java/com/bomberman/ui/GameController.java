package com.bomberman.ui;

import com.bomberman.game.GameEngine;
import com.bomberman.model.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label viesLabel;
    @FXML private Label bombesLabel;

    private GameEngine gameEngine;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private static final int TAILLE_CASE = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameEngine = new GameEngine();
        gc = gameCanvas.getGraphicsContext2D();

        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(this::gererTouches);

        demarrerBoucleJeu();
    }

    private void demarrerBoucleJeu() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameEngine.mettreAJour();
                dessiner();
                mettreAJourUI();
            }
        };
        gameLoop.start();
    }

    private void gererTouches(KeyEvent event) {
        switch (event.getCode()) {
            case Z: case UP:
                gameEngine.deplacerJoueur(0, -1);
                break;
            case S: case DOWN:
                gameEngine.deplacerJoueur(0, 1);
                break;
            case Q: case LEFT:
                gameEngine.deplacerJoueur(-1, 0);
                break;
            case D: case RIGHT:
                gameEngine.deplacerJoueur(1, 0);
                break;
            case SPACE:
                gameEngine.poserBombe();
                break;
        }
        event.consume();
    }

    private void dessiner() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        Plateau plateau = gameEngine.getPlateau();

        // Dessiner le plateau
        for (int x = 0; x < plateau.getLargeur(); x++) {
            for (int y = 0; y < plateau.getHauteur(); y++) {
                dessinerCase(plateau.getCase(x, y));
            }
        }

        // Dessiner les bonus
        for (Bonus bonus : plateau.getBonus()) {
            if (!bonus.isConsomme()) {
                dessinerBonus(bonus);
            }
        }

        // Dessiner les bombes
        for (Bombe bombe : plateau.getBombes()) {
            dessinerBombe(bombe);
        }

        // Dessiner les explosions
        for (CaseExplosion explosion : plateau.getExplosions()) {
            dessinerExplosion(explosion);
        }

        // Dessiner le joueur
        if (gameEngine.getJoueur().isVivant()) {
            dessinerJoueur(gameEngine.getJoueur());
        }

        // Dessiner les bots
        for (Bot bot : gameEngine.getBots()) {
            if (bot.isVivant()) {
                dessinerBot(bot);
            }
        }
    }

    private void dessinerCase(Case c) {
        double x = c.getX() * TAILLE_CASE;
        double y = c.getY() * TAILLE_CASE;

        switch (c.getType()) {
            case "JOUABLE":
                gc.setFill(Color.LIGHTGREEN);
                break;
            case "CASSABLE":
                gc.setFill(Color.BROWN);
                break;
            case "INCASSABLE":
                gc.setFill(Color.DARKGRAY);
                break;
        }

        gc.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, TAILLE_CASE, TAILLE_CASE);
    }

    private void dessinerJoueur(Joueur joueur) {
        double x = joueur.getX() * TAILLE_CASE + 5;
        double y = joueur.getY() * TAILLE_CASE + 5;

        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, TAILLE_CASE - 10, TAILLE_CASE - 10);
    }

    private void dessinerBot(Bot bot) {
        double x = bot.getX() * TAILLE_CASE + 5;
        double y = bot.getY() * TAILLE_CASE + 5;

        gc.setFill(Color.RED);
        gc.fillOval(x, y, TAILLE_CASE - 10, TAILLE_CASE - 10);
    }

    private void dessinerBombe(Bombe bombe) {
        double x = bombe.getX() * TAILLE_CASE + 8;
        double y = bombe.getY() * TAILLE_CASE + 8;

        gc.setFill(Color.BLACK);
        gc.fillOval(x, y, TAILLE_CASE - 16, TAILLE_CASE - 16);
    }

    private void dessinerExplosion(CaseExplosion explosion) {
        double x = explosion.getX() * TAILLE_CASE;
        double y = explosion.getY() * TAILLE_CASE;

        gc.setFill(Color.ORANGE);
        gc.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);
    }

    private void dessinerBonus(Bonus bonus) {
        double x = bonus.getX() * TAILLE_CASE + 10;
        double y = bonus.getY() * TAILLE_CASE + 10;

        switch (bonus.getType()) {
            case BOMBE_PLUS:
                gc.setFill(Color.YELLOW);
                break;
            case PORTEE_PLUS:
                gc.setFill(Color.PURPLE);
                break;
            case VIE_PLUS:
                gc.setFill(Color.PINK);
                break;
        }

        gc.fillRect(x, y, TAILLE_CASE - 20, TAILLE_CASE - 20);
    }

    private void mettreAJourUI() {
        Joueur joueur = gameEngine.getJoueur();
        scoreLabel.setText("Score: " + joueur.getScore());
        viesLabel.setText("Vies: " + joueur.getVies());
        bombesLabel.setText("Bombes: " + joueur.getNombreBombes());
    }

    @FXML
    private void nouvellePartie() {
        gameEngine.nouvellePartie();
    }

    @FXML
    private void afficherScores() {
        // Logique pour afficher les scores
        System.out.println("=== TOP SCORES ===");
        for (var score : gameEngine.getScoreManager().getTopScores()) {
            System.out.println(score.getNom() + ": " + score.getScore());
        }
    }
}

