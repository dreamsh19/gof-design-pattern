package com.dremash19.mazegameapp.model;

import static com.dremash19.mazegameapp.model.Direction.*;

public class StandardMazeBuilder implements MazeBuilder {
    private Maze currentMaze;

    public StandardMazeBuilder() {
    }

    @Override
    public void buildMaze() {
        currentMaze = new Maze();
    }

    @Override
    public void buildRoom(int n) {

        if (currentMaze.roomNumber(n) != null) {

            Room room = new Room(n);
            room.setSide(NORTH, new Wall());
            room.setSide(SOUTH, new Wall());
            room.setSide(EAST, new Wall());
            room.setSide(WEST, new Wall());

            currentMaze.addRoom(room);
        }
    }

    @Override
    public void buildDoor(int n1, int n2) {
        Room r1 = currentMaze.roomNumber(n1);
        Room r2 = currentMaze.roomNumber(n2);

        if (r1 != null && r2 != null) {
            Door door = new Door(r1, r2);
            r1.setSide(commonWall(r1, r2), door);
            r2.setSide(commonWall(r2, r1), door);
        }
    }

    @Override
    public Maze getMaze() {
        return currentMaze;
    }

    private Direction commonWall(Room r1, Room r2) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
