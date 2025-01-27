package com.dremash19.mazegameapp.model;

public interface MazeFactory {

    Maze makeMaze();

    Wall makeWall();

    Room makeRoom(int n);

    Door makeDoor(Room r1, Room r2);
}