package com.dremash19.mazegameapp;

import com.dremash19.mazegameapp.model.*;
import org.springframework.stereotype.Component;

import static com.dremash19.mazegameapp.model.Direction.*;

@Component
public class MazeGame{

    private MazeGame() {
    }

    public Maze createMaze() {

        Maze maze = new Maze();

        Room r1 = new Room(1);
        Room r2 = new Room(2);
        Door door = new Door(r1, r2);

        maze.addRoom(r1);
        maze.addRoom(r2);

        r1.setSide(NORTH, new Wall());
        r1.setSide(EAST, door);
        r1.setSide(SOUTH, new Wall());
        r1.setSide(WEST, new Wall());

        r2.setSide(NORTH, new Wall());
        r2.setSide(EAST, new Wall());
        r2.setSide(SOUTH, new Wall());
        r2.setSide(WEST, door);

        return maze;
    }

    public Maze createMazeByBuilder(MazeBuilder mazeBuilder) {
        mazeBuilder.buildMaze();
        mazeBuilder.buildRoom(1);
        mazeBuilder.buildRoom(2);
        mazeBuilder.buildDoor(1, 2);
        return mazeBuilder.getMaze();
    }

    public Maze createComplexMazeByBuilder(MazeBuilder mazeBuilder) {
        for (int i = 0; i < 1000; i++) {
            mazeBuilder.buildRoom(i);
        }
        return mazeBuilder.getMaze();
    }
}
