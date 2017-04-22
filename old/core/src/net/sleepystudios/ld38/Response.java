package net.sleepystudios.ld38;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;

/**
 * Created by Tudor on 22/04/2017.
 */
public class Response {
    private Net.HttpResponse res;
    private LD38 game;
    String resObj;

    public Response(Net.HttpResponse res, LD38 game) {
        this.res = res;
        this.game = game;
        resObj = res.getResultAsString();
    }

    public int getStatusCode() {
        return res.getStatus().getStatusCode();
    }

    public boolean failed() {
        return getStatusCode() != HttpStatus.SC_OK;
    }

    public String getStr(Object key) {
        return game.getStr(resObj, key);
    }
}
