package com.bomberman.model;

public class Bombe {
    private int x, y;
    private int portee;
    private long tempsCreation;
    private boolean explosee;

    public Bombe(int x, int y, int portee) {
        this.x = x;
        this.y = y;
        this.portee = portee;
        this.tempsCreation = System.currentTimeMillis();
        this.explosee = false;
    }

    public boolean doitExploser() {
        return System.currentTimeMillis() - tempsCreation > 3000 && !explosee; // 3 secondes
    }

    public void exploser() {
        explosee = true;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getPortee() { return portee; }
    public boolean isExplosee() { return explosee; }
}
