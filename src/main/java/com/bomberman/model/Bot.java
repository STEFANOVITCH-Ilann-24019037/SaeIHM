package com.bomberman.model;

import java.util.Random;

public class Bot {
    private int x, y;
    private boolean vivant;
    private Random random;
    private long dernierMouvement;

    public Bot(int x, int y) {
        this.x = x;
        this.y = y;
        this.vivant = true;
        this.random = new Random();
        this.dernierMouvement = 0;
    }

    public void deplacerAleatoirement(Plateau plateau) {
        if (System.currentTimeMillis() - dernierMouvement < 800) return;

        int direction = random.nextInt(4);
        int newX = x, newY = y;

        switch (direction) {
            case 0: newY--; break; // Haut
            case 1: newY++; break; // Bas
            case 2: newX--; break; // Gauche
            case 3: newX++; break; // Droite
        }

        if (plateau.isValidPosition(newX, newY) && plateau.getCase(newX, newY).isTraversable()) {
            this.x = newX;
            this.y = newY;
            dernierMouvement = System.currentTimeMillis();
        }
    }

    public void mourir() {
        vivant = false;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVivant() { return vivant; }
}
