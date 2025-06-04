package com.bomberman.game;

import com.bomberman.model.*;
import java.util.*;

public class GameEngine {
    private Plateau plateau;
    private Joueur joueur;
    private List<Bot> bots;
    private ScoreManager scoreManager;
    private boolean jeuEnCours;

    public GameEngine() {
        this.scoreManager = new ScoreManager();
        nouvellePartie();
    }

    public void nouvellePartie() {
        plateau = new Plateau(15, 13);
        joueur = new Joueur(1, 1);
        bots = new ArrayList<>();

        // Ajouter des bots
        bots.add(new Bot(13, 11));
        bots.add(new Bot(13, 1));
        bots.add(new Bot(1, 11));

        jeuEnCours = true;
    }

    public void mettreAJour() {
        if (!jeuEnCours) return;

        plateau.mettreAJour();

        // Déplacer les bots
        for (Bot bot : bots) {
            if (bot.isVivant()) {
                bot.deplacerAleatoirement(plateau);
            }
        }

        // Vérifier collisions avec explosions
        verifierCollisionsExplosions();

        // Vérifier ramassage de bonus
        verifierBonus();

        // Vérifier conditions de fin
        verifierFinJeu();
    }

    public boolean deplacerJoueur(int deltaX, int deltaY) {
        if (!jeuEnCours || !joueur.isVivant()) return false;

        int newX = joueur.getX() + deltaX;
        int newY = joueur.getY() + deltaY;

        if (plateau.isValidPosition(newX, newY) && plateau.getCase(newX, newY).isTraversable()) {
            joueur.deplacer(newX, newY);
            return true;
        }
        return false;
    }

    public boolean poserBombe() {
        if (!jeuEnCours || !joueur.isVivant()) return false;

        // Vérifier s'il y a déjà une bombe à cette position
        boolean bombeExistante = plateau.getBombes().stream()
                .anyMatch(b -> b.getX() == joueur.getX() && b.getY() == joueur.getY());

        if (!bombeExistante && plateau.getBombes().size() < joueur.getNombreBombes()) {
            Bombe bombe = new Bombe(joueur.getX(), joueur.getY(), joueur.getPortee());
            plateau.ajouterBombe(bombe);
            return true;
        }
        return false;
    }

    private void verifierCollisionsExplosions() {
        // Vérifier joueur
        if (plateau.estDansExplosion(joueur.getX(), joueur.getY())) {
            joueur.prendreDegats();
        }

        // Vérifier bots
        for (Bot bot : bots) {
            if (bot.isVivant() && plateau.estDansExplosion(bot.getX(), bot.getY())) {
                bot.mourir();
                joueur.ajouterScore(100);
            }
        }
    }

    private void verifierBonus() {
        Bonus bonus = plateau.getBonusAt(joueur.getX(), joueur.getY());
        if (bonus != null) {
            bonus.consommer();
            appliquerBonus(bonus);
        }
    }

    private void appliquerBonus(Bonus bonus) {
        switch (bonus.getType()) {
            case BOMBE_PLUS:
                joueur.setNombreBombes(joueur.getNombreBombes() + 1);
                break;
            case PORTEE_PLUS:
                joueur.setPortee(joueur.getPortee() + 1);
                break;
            case VIE_PLUS:
                // Logique pour vie supplémentaire
                break;
        }
        joueur.ajouterScore(50);
    }

    private void verifierFinJeu() {
        if (!joueur.isVivant()) {
            jeuEnCours = false;
            scoreManager.ajouterScore(joueur.getScore(), "Joueur");
        } else if (bots.stream().noneMatch(Bot::isVivant)) {
            jeuEnCours = false;
            joueur.ajouterScore(500); // Bonus de victoire
            scoreManager.ajouterScore(joueur.getScore(), "Joueur");
        }
    }

    // Getters
    public Plateau getPlateau() { return plateau; }
    public Joueur getJoueur() { return joueur; }
    public List<Bot> getBots() { return bots; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public boolean isJeuEnCours() { return jeuEnCours; }
}
