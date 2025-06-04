package com.bomberman.model;

public abstract class Case {
    protected int x, y;
    protected boolean traversable;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isTraversable() { return traversable; }
    public abstract String getType();
}



