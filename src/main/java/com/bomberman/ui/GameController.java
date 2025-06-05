package com.bomberman.ui;

import com.bomberman.game.GameEngine;
import com.bomberman.model.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label viesLabel;
    @FXML private Label bombesLabel;
    @FXML private Label scoreLabel2;
    @FXML private Label viesLabel2;
    @FXML private Label bombesLabel2;
    @FXML private Label bonusCountLabel;
    @FXML private Button modeButton;

    private GameEngine gameEngine;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private ImageManager imageManager;
    private static final int TAILLE_CASE = 30;
    private boolean modeMultijoueur = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageManager = ImageManager.getInstance();
        gameEngine = new GameEngine(modeMultijoueur);
        gc = gameCanvas.getGraphicsContext2D();

        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(this::gererTouches);

        // Initialiser l'affichage du mode
        mettreAJourModeUI();

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
            // Contrôles Joueur 1
            case Z: case UP:
                gameEngine.deplacerJoueur(1, 0, -1);
                break;
            case S: case DOWN:
                gameEngine.deplacerJoueur(1, 0, 1);
                break;
            case Q: case LEFT:
                gameEngine.deplacerJoueur(1, -1, 0);
                break;
            case D: case RIGHT:
                gameEngine.deplacerJoueur(1, 1, 0);
                break;
            case SPACE:
                gameEngine.poserBombe(1);
                break;

            // Contrôles Joueur 2 (mode multijoueur)
            case I:
                if (modeMultijoueur) {
                    gameEngine.deplacerJoueur(2, 0, -1);
                }
                break;
            case K:
                if (modeMultijoueur) {
                    gameEngine.deplacerJoueur(2, 0, 1);
                }
                break;
            case J:
                if (modeMultijoueur) {
                    gameEngine.deplacerJoueur(2, -1, 0);
                }
                break;
            case L:
                if (modeMultijoueur) {
                    gameEngine.deplacerJoueur(2, 1, 0);
                }
                break;
            case ENTER:
                if (modeMultijoueur) {
                    gameEngine.poserBombe(2);
                }
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

        // Dessiner les bonus (limités à 6)
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

        // Dessiner les joueurs
        if (gameEngine.getJoueur1() != null && gameEngine.getJoueur1().isVivant()) {
            dessinerJoueur(gameEngine.getJoueur1(), 1);
        }

        if (modeMultijoueur && gameEngine.getJoueur2() != null && gameEngine.getJoueur2().isVivant()) {
            dessinerJoueur(gameEngine.getJoueur2(), 2);
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

        Image image = imageManager.getImage(c.getType());
        if (image != null) {
            gc.drawImage(image, x, y, TAILLE_CASE, TAILLE_CASE);
        } else {
            // Fallback avec couleurs
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
                default:
                    gc.setFill(Color.WHITE);
                    break;
            }
            gc.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, TAILLE_CASE, TAILLE_CASE);
        }
    }

    private void dessinerJoueur(Joueur joueur, int numeroJoueur) {
        double x = joueur.getX() * TAILLE_CASE;
        double y = joueur.getY() * TAILLE_CASE;

        String imageKey = "JOUEUR" + numeroJoueur;
        Image image = imageManager.getImage(imageKey);

        if (image != null) {
            gc.drawImage(image, x, y, TAILLE_CASE, TAILLE_CASE);
        } else {
            // Fallback avec couleurs
            x += 5;
            y += 5;
            gc.setFill(numeroJoueur == 1 ? Color.BLUE : Color.CYAN);
            gc.fillOval(x, y, TAILLE_CASE - 10, TAILLE_CASE - 10);
        }

        // Effet d'invincibilité (clignotement)
        if (joueur.isInvincible()) {
            gc.setFill(Color.YELLOW);
            gc.setGlobalAlpha(0.5);
            gc.fillOval(joueur.getX() * TAILLE_CASE + 2, joueur.getY() * TAILLE_CASE + 2,
                    TAILLE_CASE - 4, TAILLE_CASE - 4);
            gc.setGlobalAlpha(1.0);
        }
    }

    private void dessinerBot(Bot bot) {
        double x = bot.getX() * TAILLE_CASE;
        double y = bot.getY() * TAILLE_CASE;

        Image image = imageManager.getImage("BOT");
        if (image != null) {
            gc.drawImage(image, x, y, TAILLE_CASE, TAILLE_CASE);
        } else {
            // Fallback avec couleur
            x += 5;
            y += 5;
            gc.setFill(Color.RED);
            gc.fillOval(x, y, TAILLE_CASE - 10, TAILLE_CASE - 10);
        }
    }

    private void dessinerBombe(Bombe bombe) {
        double x = bombe.getX() * TAILLE_CASE;
        double y = bombe.getY() * TAILLE_CASE;

        Image image = imageManager.getImage("BOMBE");
        if (image != null) {
            gc.drawImage(image, x, y, TAILLE_CASE, TAILLE_CASE);
        } else {
            // Fallback avec couleur
            x += 8;
            y += 8;
            gc.setFill(Color.BLACK);
            gc.fillOval(x, y, TAILLE_CASE - 16, TAILLE_CASE - 16);
        }
    }

    private void dessinerExplosion(CaseExplosion explosion) {
        double x = explosion.getX() * TAILLE_CASE;
        double y = explosion.getY() * TAILLE_CASE;

        Image image = imageManager.getImage("EXPLOSION");
        if (image != null) {
            gc.drawImage(image, x, y, TAILLE_CASE, TAILLE_CASE);
        } else {
            // Fallback avec couleur
            gc.setFill(Color.ORANGE);
            gc.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);
        }
    }

    private void dessinerBonus(Bonus bonus) {
        double x = bonus.getX() * TAILLE_CASE;
        double y = bonus.getY() * TAILLE_CASE;

        String imageKey = bonus.getType().toString();
        Image image = imageManager.getImage(imageKey);

        if (image != null) {
            gc.drawImage(image, x, y, TAILLE_CASE, TAILLE_CASE);
        } else {
            // Fallback avec couleurs
            x += 10;
            y += 10;
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
                case VITESSE_PLUS:
                    gc.setFill(Color.LIGHTBLUE);
                    break;
                case INVINCIBLE:
                    gc.setFill(Color.GOLD);
                    break;
                case MEGA_BOMBE:
                    gc.setFill(Color.DARKRED);
                    break;
                default:
                    gc.setFill(Color.WHITE);
                    break;
            }
            gc.fillRect(x, y, TAILLE_CASE - 20, TAILLE_CASE - 20);
        }
    }

    private void mettreAJourUI() {
        Joueur joueur1 = gameEngine.getJoueur1();
        if (joueur1 != null) {
            scoreLabel.setText("J1 Score: " + joueur1.getScore());
            viesLabel.setText("J1 Vies: " + joueur1.getVies());
            bombesLabel.setText("J1 Bombes: " + joueur1.getNombreBombes());
        }

        if (modeMultijoueur) {
            Joueur joueur2 = gameEngine.getJoueur2();
            if (joueur2 != null) {
                scoreLabel2.setText("J2 Score: " + joueur2.getScore());
                viesLabel2.setText("J2 Vies: " + joueur2.getVies());
                bombesLabel2.setText("J2 Bombes: " + joueur2.getNombreBombes());

                // Rendre visible les labels du joueur 2
                if (scoreLabel2 != null) scoreLabel2.setVisible(true);
                if (viesLabel2 != null) viesLabel2.setVisible(true);
                if (bombesLabel2 != null) bombesLabel2.setVisible(true);
            }
        } else {
            // Cacher les labels du joueur 2 en mode solo
            if (scoreLabel2 != null) scoreLabel2.setVisible(false);
            if (viesLabel2 != null) viesLabel2.setVisible(false);
            if (bombesLabel2 != null) bombesLabel2.setVisible(false);
        }

        // Afficher le nombre de bonus actifs (limité à 6)
        if (bonusCountLabel != null) {
            int bonusActifs = gameEngine.getPlateau().getNombreBonusActifs();
            bonusCountLabel.setText("Bonus: " + bonusActifs + "/6");
        }

        // Vérifier fin de partie
        if (!gameEngine.isJeuEnCours()) {
            afficherFinPartie();
        }
    }

    private void afficherFinPartie() {
        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(0.7);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gc.setGlobalAlpha(1.0);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(24));

        String message = "GAME OVER";
        if (modeMultijoueur) {
            Joueur joueur1 = gameEngine.getJoueur1();
            Joueur joueur2 = gameEngine.getJoueur2();

            if (joueur1 != null && joueur1.isVivant() && (joueur2 == null || !joueur2.isVivant())) {
                message = "JOUEUR 1 GAGNE!";
            } else if (joueur2 != null && joueur2.isVivant() && (joueur1 == null || !joueur1.isVivant())) {
                message = "JOUEUR 2 GAGNE!";
            } else if (gameEngine.getBots().stream().noneMatch(Bot::isVivant)) {
                message = "VICTOIRE!";
            }
        } else {
            if (gameEngine.getBots().stream().noneMatch(Bot::isVivant)) {
                message = "VICTOIRE!";
            }
        }

        gc.fillText(message, gameCanvas.getWidth()/2 - 80, gameCanvas.getHeight()/2);
    }

    @FXML
    private void nouvellePartie() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameEngine = new GameEngine(modeMultijoueur);
        mettreAJourModeUI();
        demarrerBoucleJeu();
    }

    @FXML
    private void changerMode() {
        modeMultijoueur = !modeMultijoueur;
        nouvellePartie();
    }

    private void mettreAJourModeUI() {
        if (modeButton != null) {
            modeButton.setText(modeMultijoueur ? "Mode: 2 Joueurs" : "Mode: Solo");
        }
    }

    @FXML
    private void afficherScores() {
        System.out.println("=== TOP SCORES ===");
        for (var score : gameEngine.getScoreManager().getTopScores()) {
            System.out.println(score.getNom() + ": " + score.getScore());
        }
    }

    @FXML
    private void afficherControles() {
        System.out.println("=== CONTROLES ===");
        System.out.println("Joueur 1: ZQSD + ESPACE");
        if (modeMultijoueur) {
            System.out.println("Joueur 2: IJKL + ENTREE");
        }
        System.out.println("Bonus limités à 6 simultanément");
    }
}