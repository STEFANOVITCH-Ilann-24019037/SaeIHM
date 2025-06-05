package com.bomberman.ui;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static ImageManager instance;
    private Map<String, Image> images;

    private ImageManager() {
        images = new HashMap<>();
        chargerImages();
    }

    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    private void chargerImages() {
        try {
            // Images du plateau
            images.put("JOUABLE", new Image("/images/grass.png"));
            images.put("CASSABLE", new Image("/images/brick.png"));
            images.put("INCASSABLE", new Image("/images/wall.png"));

            // Images des personnages
            images.put("JOUEUR1", new Image("/images/player1.png"));
            images.put("JOUEUR2", new Image("/images/player2.png"));
            images.put("BOT", new Image("/images/enemy.png"));

            // Images des objets
            images.put("BOMBE", new Image("/images/bomb.png"));
            images.put("EXPLOSION", new Image("/images/explosion.png"));

            // Images des bonus
            images.put("BOMBE_PLUS", new Image("/images/bonus_bomb.png"));
            images.put("PORTEE_PLUS", new Image("/images/bonus_range.png"));
            images.put("VIE_PLUS", new Image("/images/bonus_life.png"));
            images.put("VITESSE_PLUS", new Image("/images/bonus_speed.png"));
            images.put("INVINCIBLE", new Image("/images/bonus_invincible.png"));
            images.put("MEGA_BOMBE", new Image("/images/bonus_mega.png"));

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
            // Créer des images par défaut en cas d'erreur
            creerImagesParDefaut();
        }
    }

    private void creerImagesParDefaut() {
        // En cas d'erreur, on peut créer des images colorées simples
        // ou utiliser des couleurs par défaut dans le GameController
    }

    public Image getImage(String nom) {
        return images.get(nom);
    }

    public boolean hasImage(String nom) {
        return images.containsKey(nom) && images.get(nom) != null;
    }
}