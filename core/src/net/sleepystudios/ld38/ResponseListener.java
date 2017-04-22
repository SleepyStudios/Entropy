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
                ArrayList<String> ids = new ArrayList<String>();
                for(int i=0; i<arr.size(); i++) {
                    // make sure they're not a dupe
                    String id = game.getStr(arr.get(i).toString(), "id");
                    if(!dupe(id)) {
                        game.players.add(new Player(game, arr.get(i).toString()));
                    } else {
                        // update position if they are
                        if(!id.equals(game.me)) {
                            game.getPlayerByID(id).x = game.getInt(arr.get(i).toString(), "x");
                            game.getPlayerByID(id).y = game.getInt(arr.get(i).toString(), "y");
                        }
                    }

                    ids.add(id);
                }

                // compare for removals
                for(int i=0; i<game.players.size(); i++) {
                    if(!ids.contains(game.players.get(i).id)) game.players.remove(i);
                }
            }
        }
    }

    private boolean dupe(String id) {
        for(int i=0; i<game.players.size(); i++) {
            if(game.players.get(i).id.equals(id)) return true;
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
