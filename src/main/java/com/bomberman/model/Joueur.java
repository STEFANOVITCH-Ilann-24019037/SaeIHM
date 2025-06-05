package com.bomberman.model;

public class Joueur {
    private String nom;
    private int x, y;
    private int vies;
    private int score;
    private int nombreBombes;
    private int portee;
    private boolean vivant;
    private int vitesse; // Délai entre les mouvements en ms
    private boolean invincible;
    private long finInvincibilite;
    private boolean megaBombe;
    private long dernierMouvement;
    private int numeroJoueur; // 1 ou 2 pour le multijoueur

    public Joueur(int x, int y, int numeroJoueur) {
        this.x = x;
        this.y = y;
        this.numeroJoueur = numeroJoueur;
        this.nom = "Joueur " + numeroJoueur;
        this.vies = 3;
        this.score = 0;
        this.nombreBombes = 1;
        this.portee = 2;
        this.vivant = true;
        this.vitesse = 200; // 200ms entre les mouvements
        this.invincible = false;
        this.finInvincibilite = 0;
        this.megaBombe = false;
        this.dernierMouvement = 0;
    }

    public boolean peutBouger() {
        return System.currentTimeMillis() - dernierMouvement >= vitesse;
    }

    public void deplacer(int newX, int newY) {
        if (peutBouger()) {
            this.x = newX;
            this.y = newY;
            this.dernierMouvement = System.currentTimeMillis();
        }
    }

    public void prendreDegats() {
        if (invincible && System.currentTimeMillis() < finInvincibilite) {
            return; // Pas de dégâts si invincible
        }

        vies--;
        if (vies <= 0) {
            vivant = false;
        } else {
            // Invincibilité temporaire après avoir pris des dégâts
            activerInvincibilite(2000); // 2 secondes
        }
    }

    public void activerInvincibilite(long duree) {
        this.invincible = true;
        this.finInvincibilite = System.currentTimeMillis() + duree;
    }

    public void ajouterScore(int points) {
        score += points;
    }

    public boolean isInvincible() {
        if (invincible && System.currentTimeMillis() >= finInvincibilite) {
            invincible = false;
        }
        return invincible;
    }

    public void activerMegaBombe() {
        this.megaBombe = true;
    }

    public boolean hasMegaBombe() {
        return megaBombe;
    }

    public void utiliserMegaBombe() {
        this.megaBombe = false;
    }

    // Getters et setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getVies() { return vies; }
    public int getScore() { return score; }
    public int getNombreBombes() { return nombreBombes; }
    public int getPortee() { return portee; }
    public boolean isVivant() { return vivant; }
    public int getVitesse() { return vitesse; }
    public int getNumeroJoueur() { return numeroJoueur; }
    public String getNom() { return nom; }

    public void setNombreBombes(int nombreBombes) {
        this.nombreBombes = Math.min(nombreBombes, 5); // Max 5 bombes
    }

    public void setPortee(int portee) {
        this.portee = Math.min(portee, 8); // Max portée 8
    }

    public void setVitesse(int vitesse) {
        this.vitesse = Math.max(vitesse, 50); // Min 50ms
    }

    public void setVies(int vies) {
        this.vies = Math.min(vies, 9); // Max 9 vies
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}