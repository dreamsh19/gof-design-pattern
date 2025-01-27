package com.dremash19.mazegameapp;

import com.dremash19.mazegameapp.model.Door;
import com.dremash19.mazegameapp.model.Maze;
import com.dremash19.mazegameapp.model.Room;
import com.dremash19.mazegameapp.model.Wall;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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

}
