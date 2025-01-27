package com.dremash19.mazegameapp.model;

public class Door implements MapSite {

    private final Room r1, r2;
    private boolean isOpen;

    public Door(Room r1, Room r2) {
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public void enter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
