package com.dremash19.mazegameapp.model;

public class Maze implements Cloneable {
    public void addRoom(Room room){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Room roomNumber(int roomNumber){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Maze clone() {
        try {
            return (Maze) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
