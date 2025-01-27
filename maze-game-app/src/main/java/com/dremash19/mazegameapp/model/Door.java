package com.dremash19.mazegameapp.model;

public abstract class Door implements MapSite {

    private final Room r1, r2;
    private boolean isOpen;

    protected Door(Room r1, Room r2) {
        this.r1 = r1;
        this.r2 = r2;
    }
}
