package com.dremash19.mazegameapp;

import com.dremash19.mazegameapp.model.Door;
import com.dremash19.mazegameapp.model.Maze;
import com.dremash19.mazegameapp.model.Room;
import com.dremash19.mazegameapp.model.Wall;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.dremash19.mazegameapp.model.Direction.*;

public class MazeGame {

    @Configuration
    public static class Singleton {
        private static final MazeGame INSTANCE = new MazeGame();

        @Bean
        public static MazeGame getInstance() {
            return INSTANCE;
        }

    }

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
