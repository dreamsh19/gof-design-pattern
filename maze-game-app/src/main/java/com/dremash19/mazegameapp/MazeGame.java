package com.dremash19.mazegameapp;

import com.dremash19.mazegameapp.model.Door;
import com.dremash19.mazegameapp.model.Maze;
import com.dremash19.mazegameapp.model.MazeFactory;
import com.dremash19.mazegameapp.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dremash19.mazegameapp.model.Direction.*;

@Component
public class MazeGame {

    private final MazeFactory mazeFactory;

    @Autowired
    private MazeGame(MazeFactory mazeFactory) {
        this.mazeFactory = mazeFactory;
    }

    public Maze createMaze() {

        Maze maze = mazeFactory.makeMaze();


        Room r1 = mazeFactory.makeRoom(1);
        Room r2 = mazeFactory.makeRoom(2);
        Door door = mazeFactory.makeDoor(r1, r2);

        maze.addRoom(r1);
        maze.addRoom(r2);

        r1.setSide(NORTH, mazeFactory.makeWall());
        r1.setSide(EAST, door);
        r1.setSide(SOUTH, mazeFactory.makeWall());
        r1.setSide(WEST, mazeFactory.makeWall());

        r2.setSide(NORTH, mazeFactory.makeWall());
        r2.setSide(EAST, mazeFactory.makeWall());
        r2.setSide(SOUTH, mazeFactory.makeWall());
        r2.setSide(WEST, door);

        return maze;
    }

}
