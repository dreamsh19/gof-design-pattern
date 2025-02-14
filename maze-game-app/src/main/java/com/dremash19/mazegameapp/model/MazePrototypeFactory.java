package com.dremash19.mazegameapp.model;

public abstract class MazePrototypeFactory implements MazeFactory {

    private final Maze mazePrototype;
    private final Wall wallPrototype;
    private final Room roomPrototype;
    private final Door doorPrototype;

    protected MazePrototypeFactory(Maze mazePrototype, Wall wallPrototype, Room roomPrototype, Door doorPrototype) {
        this.mazePrototype = mazePrototype;
        this.wallPrototype = wallPrototype;
        this.roomPrototype = roomPrototype;
        this.doorPrototype = doorPrototype;
    }

    @Override
    public Maze makeMaze() {
        return mazePrototype.clone();
    }

    @Override
    public Wall makeWall() {
        return wallPrototype.clone();
    }

    @Override
    public Room makeRoom(int n) {
        Room room = roomPrototype.clone();
        room.initialize(n);
        return room;
    }

    @Override
    public Door makeDoor(Room r1, Room r2) {
        Door door = doorPrototype.clone();
        door.initialize(r1, r2);
        return door;
    }

}
