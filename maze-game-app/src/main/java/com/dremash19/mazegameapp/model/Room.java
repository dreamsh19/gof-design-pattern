package com.dremash19.mazegameapp.model;

public class Room implements MapSite, Cloneable {

    private int roomNumber;
    private final MapSite[] sides = new MapSite[4];

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public MapSite getSide(Direction direction){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MapSite setSide(Direction direction, MapSite side){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void enter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Room clone() {
        try {
            final Room clone = (Room) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void initialize(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}