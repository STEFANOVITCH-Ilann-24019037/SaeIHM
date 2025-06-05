package com.bomberman.model;

public class Bonus {
    private int x, y;
    private TypeBonus type;
    private boolean consomme;

    public enum TypeBonus {
        BOMBE_PLUS,     // +1 bombe
        PORTEE_PLUS,    // +1 portée
        VIE_PLUS,       // +1 vie
        VITESSE_PLUS,   // Augmente la vitesse de déplacement
        INVINCIBLE,     // Invincibilité temporaire (5 secondes)
        MEGA_BOMBE      // Prochaine bombe a une portée maximale
    }

    public Bonus(int x, int y, TypeBonus type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.consomme = false;
    }

    public void consommer() {
        consomme = true;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public TypeBonus getType() { return type; }
    public boolean isConsomme() { return consomme; }
}