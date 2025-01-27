package com.dremash19.mazegameapp.model;

public abstract class Maze {
    public abstract void addRoom(Room room);

    protected abstract Room roomNumber(int roomNumber);
}
