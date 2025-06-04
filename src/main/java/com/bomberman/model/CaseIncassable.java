package com.bomberman.model;

public class CaseIncassable extends Case {
    public CaseIncassable(int x, int y) {
        super(x, y);
        this.traversable = false;
    }

    @Override
    public String getType() {
        return "INCASSABLE";
    }
}

