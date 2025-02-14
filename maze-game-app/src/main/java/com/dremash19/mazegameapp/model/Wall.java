package com.dremash19.mazegameapp.model;

public class Wall implements MapSite, Cloneable {

    public Wall() {

    }

    @Override
    public void enter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Wall clone() {
        try {
            return (Wall) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
