package com.bomberman.model;

public class CaseExplosion extends Case {
    private long tempsExplosion;

    public CaseExplosion(int x, int y) {
        super(x, y);
        this.traversable = true;
        this.tempsExplosion = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - tempsExplosion > 500; // 500ms
    }

    @Override
    public String getType() {
        return "EXPLOSION";
    }
}