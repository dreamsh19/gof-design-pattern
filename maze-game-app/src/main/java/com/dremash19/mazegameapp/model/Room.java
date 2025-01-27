package com.dremash19.mazegameapp.model;

public class Room implements MapSite {

    private final int roomNumber;
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
}