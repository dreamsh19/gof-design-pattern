package com.dremash19.mazegameapp.model;

public class CountingMazeBuilder implements MazeBuilder {
    private int rooms;
    private int doors;

    public CountingMazeBuilder() {
        rooms = doors = 0;
    }

    @Override
    public void buildRoom(int n) {
        rooms++;
    }

    @Override
    public void buildDoor(int n1, int n2) {
        doors++;
    }

    public int getRoomCount(){
        return rooms;
    }

    public int getDoorCount(){
        return doors;
    }
}
