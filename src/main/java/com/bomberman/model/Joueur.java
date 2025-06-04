package com.bomberman.model;

public class Joueur {
    String nom;
    private int x, y;
    private int vies;
    private int score;
    private int nombreBombes;
    private int portee;
    private boolean vivant;

    public Joueur(int x, int y) {
        this.x = x;
        this.y = y;
        this.vies = 3;
        this.score = 0;
        this.nombreBombes = 1;
        this.portee = 2;
        this.vivant = true;
    }

    public void deplacer(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void prendreDegats() {
        vies--;
        if (vies <= 0) {
            vivant = false;
        }
    }

    public void ajouterScore(int points) {
        score += points;
    }

    // Getters et setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getVies() { return vies; }
    public int getScore() { return score; }
    public int getNombreBombes() { return nombreBombes; }
    public int getPortee() { return portee; }
    public boolean isVivant() { return vivant; }

    public void setNombreBombes(int nombreBombes) { this.nombreBombes = nombreBombes; }
    public void setPortee(int portee) { this.portee = portee; }
}
