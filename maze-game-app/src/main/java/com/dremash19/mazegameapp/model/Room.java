package com.dremash19.mazegameapp.model;

public abstract class Room implements MapSite {

    private final int roomNumber;
    private final MapSite[] sides = new MapSite[4];

    protected Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public abstract MapSite getSide(Direction direction);

    public abstract MapSite setSide(Direction direction, MapSite side);
}