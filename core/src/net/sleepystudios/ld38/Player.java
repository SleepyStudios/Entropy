package net.sleepystudios.ld38;

/**
 * Created by Tudor on 22/04/2017.
 */
public class Player {
    private LD38 game;
    String name, id;

    public Player(LD38 game, String data) {
        this.game = game;
        name = this.game.getStr(data, "name");
        id = this.game.getStr(data, "id");
    }
}
