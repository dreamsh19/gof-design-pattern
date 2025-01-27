package com.dremash19.mazegameapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MazeGameRunner implements ApplicationRunner {

    private final MazeGame mazeGame;

    @Autowired
    private MazeGameRunner(MazeGame mazeGame) {
        this.mazeGame = mazeGame;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mazeGame.createMaze();
    }
}
