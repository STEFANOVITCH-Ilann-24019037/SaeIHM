package com.bomberman.model;

public class CaseCassable extends Case {
    private boolean detruite = false;

    public CaseCassable(int x, int y) {
        super(x, y);
        this.traversable = false;
    }

    public void detruire() {
        this.detruite = true;
        this.traversable = true;
    }

    public boolean isDetruite() { return detruite; }

    @Override
    public String getType() {
        return detruite ? "JOUABLE" : "CASSABLE";
    }
}
