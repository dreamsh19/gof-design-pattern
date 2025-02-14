package com.dremash19.mazegameapp.model;

public class Door implements MapSite, Cloneable {

    private Room r1;
    private Room r2;
    private boolean isOpen;

    public Door(Room r1, Room r2) {
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public void enter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Door clone() {
        try {
            final Door clone = (Door) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void initialize(Room r1, Room r2) {
        this.r1 = r1;
        this.r2 = r2;
    }
}
