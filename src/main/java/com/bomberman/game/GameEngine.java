package com.bomberman.game;

import com.bomberman.model.*;
import java.util.*;

public class GameEngine {
    private Plateau plateau;
    private List<Joueur> joueurs;
    private List<Bot> bots;
    private ScoreManager scoreManager;
    private boolean jeuEnCours;
    private boolean modeMultijoueur;

    public GameEngine() {
        this.scoreManager = new ScoreManager();
        this.modeMultijoueur = false;
        nouvellePartie();
    }

    public GameEngine(boolean modeMultijoueur) {
        this.scoreManager = new ScoreManager();
        this.modeMultijoueur = modeMultijoueur;
        nouvellePartie();
    }

    public void nouvellePartie() {
        plateau = new Plateau(15, 13);
        joueurs = new ArrayList<>();
        bots = new ArrayList<>();

        if (modeMultijoueur) {
            // Mode 2 joueurs
            joueurs.add(new Joueur(1, 1, 1));
            joueurs.add(new Joueur(13, 11, 2));

            // Moins de bots en multijoueur
            bots.add(new Bot(13, 1));
            bots.add(new Bot(1, 11));
        } else {
            // Mode solo
            joueurs.add(new Joueur(1, 1, 1));

            // Plus de bots en solo
            bots.add(new Bot(13, 11));
            bots.add(new Bot(13, 1));
            bots.add(new Bot(1, 11));
        }

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

    public boolean deplacerJoueur(int numeroJoueur, int deltaX, int deltaY) {
        if (!jeuEnCours) return false;

        Joueur joueur = getJoueur(numeroJoueur);
        if (joueur == null || !joueur.isVivant() || !joueur.peutBouger()) return false;

        int newX = joueur.getX() + deltaX;
        int newY = joueur.getY() + deltaY;

        if (plateau.isValidPosition(newX, newY) && plateau.getCase(newX, newY).isTraversable()) {
            // Vérifier qu'il n'y a pas d'autre joueur à cette position
            boolean positionLibre = joueurs.stream()
                    .filter(j -> j != joueur && j.isVivant())
                    .noneMatch(j -> j.getX() == newX && j.getY() == newY);

            if (positionLibre) {
                joueur.deplacer(newX, newY);
                return true;
            }
        }
        return false;
    }

    public boolean poserBombe(int numeroJoueur) {
        if (!jeuEnCours) return false;

        Joueur joueur = getJoueur(numeroJoueur);
        if (joueur == null || !joueur.isVivant()) return false;

        // Vérifier s'il y a déjà une bombe à cette position
        boolean bombeExistante = plateau.getBombes().stream()
                .anyMatch(b -> b.getX() == joueur.getX() && b.getY() == joueur.getY());

        if (!bombeExistante && plateau.getBombes().size() < joueur.getNombreBombes()) {
            int portee = joueur.getPortee();
            if (joueur.hasMegaBombe()) {
                portee = 8; // Portée maximale pour mega bombe
                joueur.utiliserMegaBombe();
            }

            Bombe bombe = new Bombe(joueur.getX(), joueur.getY(), portee);
            plateau.ajouterBombe(bombe);
            return true;
        }
        return false;
    }

    private void verifierCollisionsExplosions() {
        // Vérifier joueurs
        for (Joueur joueur : joueurs) {
            if (joueur.isVivant() && plateau.estDansExplosion(joueur.getX(), joueur.getY())) {
                joueur.prendreDegats();
            }
        }

        // Vérifier bots
        for (Bot bot : bots) {
            if (bot.isVivant() && plateau.estDansExplosion(bot.getX(), bot.getY())) {
                bot.mourir();
                // Donner des points à tous les joueurs vivants
                for (Joueur joueur : joueurs) {
                    if (joueur.isVivant()) {
                        joueur.ajouterScore(100);
                    }
                }
            }
        }
    }

    private void verifierBonus() {
        for (Joueur joueur : joueurs) {
            if (!joueur.isVivant()) continue;

            Bonus bonus = plateau.getBonusAt(joueur.getX(), joueur.getY());
            if (bonus != null) {
                bonus.consommer();
                appliquerBonus(joueur, bonus);
            }
        }
    }

    private void appliquerBonus(Joueur joueur, Bonus bonus) {
        switch (bonus.getType()) {
            case BOMBE_PLUS:
                joueur.setNombreBombes(joueur.getNombreBombes() + 1);
                break;
            case PORTEE_PLUS:
                joueur.setPortee(joueur.getPortee() + 1);
                break;
            case VIE_PLUS:
                joueur.setVies(joueur.getVies() + 1);
                break;
            case VITESSE_PLUS:
                joueur.setVitesse(joueur.getVitesse() - 30); // Réduction du délai = plus rapide
                break;
            case INVINCIBLE:
                joueur.activerInvincibilite(5000); // 5 secondes
                break;
            case MEGA_BOMBE:
                joueur.activerMegaBombe();
                break;
        }
        joueur.ajouterScore(50);
    }

    private void verifierFinJeu() {
        List<Joueur> joueursVivants = joueurs.stream()
                .filter(Joueur::isVivant)
                .toList();

        if (joueursVivants.isEmpty()) {
            // Tous les joueurs sont morts
            jeuEnCours = false;
            for (Joueur joueur : joueurs) {
                scoreManager.ajouterScore(joueur.getScore(), joueur.getNom());
            }
        } else if (bots.stream().noneMatch(Bot::isVivant)) {
            // Tous les bots sont morts - victoire
            jeuEnCours = false;
            for (Joueur joueur : joueursVivants) {
                joueur.ajouterScore(500); // Bonus de victoire
                scoreManager.ajouterScore(joueur.getScore(), joueur.getNom());
            }
        } else if (modeMultijoueur && joueursVivants.size() == 1) {
            // En multijoueur, un seul joueur survit
            jeuEnCours = false;
            Joueur gagnant = joueursVivants.get(0);
            gagnant.ajouterScore(300); // Bonus de victoire PvP
            for (Joueur joueur : joueurs) {
                scoreManager.ajouterScore(joueur.getScore(), joueur.getNom());
            }
        }
    }

    private Joueur getJoueur(int numeroJoueur) {
        return joueurs.stream()
                .filter(j -> j.getNumeroJoueur() == numeroJoueur)
                .findFirst()
                .orElse(null);
    }

    // Getters
    public Plateau getPlateau() { return plateau; }
    public List<Joueur> getJoueurs() { return joueurs; }
    public Joueur getJoueur1() { return getJoueur(1); }
    public Joueur getJoueur2() { return getJoueur(2); }
    public List<Bot> getBots() { return bots; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public boolean isJeuEnCours() { return jeuEnCours; }
    public boolean isModeMultijoueur() { return modeMultijoueur; }
}