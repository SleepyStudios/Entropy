package net.sleepystudios.ld38;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Player {
    String name;
    int id, type;
    float x = 640/2, y = 480/2;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.type = LD38.rand(0, 1);
    }
}
