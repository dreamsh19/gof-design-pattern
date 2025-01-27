package com.dremash19.mazegameapp.model;

public interface MazeBuilder {

    default void buildMaze(){

    };

    default void buildRoom(int n){

    }

    default void buildDoor(int n1, int n2){

    }

    default Maze getMaze(){
        return null;
    }
}
