package com.bomberman.model;

public class CaseJouable extends Case {
    public CaseJouable(int x, int y) {
        super(x, y);
        this.traversable = true;
    }

    @Override
    public String getType() {
        return "JOUABLE";
    }
}