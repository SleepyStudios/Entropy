package net.sleepystudios.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.json.simple.JSONObject;

/**
 * Created by Tudor on 22/04/2017.
 */
public class Player {
    private LD38 game;
    String name, id;
    long x, y;

    public Player(LD38 game, String data) {
        this.game = game;
        name = this.game.getStr(data, "name");
        id = this.game.getStr(data, "id");

        x = this.game.getInt(data, "x");
        y = this.game.getInt(data, "y");
    }

    public void update() {
        if(game.me.equals(id)) {
            long speed = 5;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                move(x, y+speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                move(x-speed, y);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                move(x, y-speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                move(x+speed, y);
            }
        }
    }

    private void move(long x, long y) {
        this.x = x;
        this.y = y;

        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("x", x);
        obj.put("y", y);
        game.sendRequest("move", "PUT", obj);
    }
}
