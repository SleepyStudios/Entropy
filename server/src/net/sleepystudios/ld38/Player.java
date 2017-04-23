package net.sleepystudios.ld38;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Player {
    int id, type=-1;
    float x = 640/2, y = 480/2;

    public Player(LD38 game, int id) {
        this.id = id;
        this.type = game.chooseType();
    }
}
