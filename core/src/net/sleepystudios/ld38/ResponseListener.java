package net.sleepystudios.ld38;

import com.badlogic.gdx.Net;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Tudor on 22/04/2017.
 */
public class ResponseListener implements Net.HttpResponseListener {
    private LD38 game;
    private Response res;

    public ResponseListener(LD38 game) {
        this.game = game;
    }

    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {
        res = new Response(httpResponse, game);

        String head = res.getStr("head");
        if(!res.failed()) {
            if(head.equals("login_accept")) {
                game.me = game.getStr(res.resObj, "id");
                game.players.add(new Player(game, res.resObj));
            }

            if(head.equals("players")) {
                // get the array
                JSONArray arr = game.getArray(res.resObj, "players");
                ArrayList<String> names = new ArrayList<String>();

                for(int i=0; i<arr.size(); i++) {
                    // make sure they're not a dupe
                    String name = game.getStr(arr.get(i).toString(), "name");
                    if(!dupe(name)) {
                        game.players.add(new Player(game, arr.get(i).toString()));
                    }

                    names.add(name);
                }

                // compare for removals
                for(int i=0; i<game.players.size(); i++) {
                    if(!names.contains(game.players.get(i).name)) game.players.remove(i);
                }
            }
        }
    }

    private boolean dupe(String name) {
        for(int i=0; i<game.players.size(); i++) {
            if(game.players.get(i).name.equals(name)) return true;
        }
        return false;
    }

    @Override
    public void failed(Throwable t) {
        System.out.println("Request failed: " + t);
    }

    @Override
    public void cancelled() {
        System.out.println("Request cancelled");
    }
}
