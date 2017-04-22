package net.sleepystudios.ld38;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LD38 extends ApplicationAdapter implements ActionListener {
	SpriteBatch batch;
	Texture img;
    BitmapFont font;
    ResponseListener rl;
    String name;
    ArrayList<Player> players = new ArrayList<Player>();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("background.bmp");
		font = new BitmapFont();
        rl = new ResponseListener(this);

		// add the new user
        String name = JOptionPane.showInputDialog(new JFrame(), "What's your name?");
		JSONObject obj = new JSONObject();
		obj.put("name", name);

		sendRequest("player", "POST", obj);
	}

	public void sendRequest(String route, String method, JSONObject reqObj) {
		Net.HttpRequest req = new Net.HttpRequest(method);
		String url = "http://localhost:3000/" + route;
		String content = "";
		if(reqObj!=null) content = new Json(JsonWriter.OutputType.json).toJson(reqObj);

        req.setUrl(url);
        req.setContent(content);
        req.setHeader("Content-Type", "application/json");
        req.setHeader("Accept", "application/json");

		Gdx.net.sendHttpRequest(req, rl);
	}

    public String getStr(String data, Object key) {
        JSONObject obj = new Json().fromJson(JSONObject.class, data);

        if(obj.get(key)!=null) {
            return obj.get(key).toString();
        } else {
            return "null";
        }
    }

    public JSONArray getArray(String data, Object key) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(data);
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.get(key)!=null) {
                JSONArray arr = (JSONArray) jsonObject.get(key);
                return arr;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    String me;
    public Player getMe() {
	    for(int i=0; i<players.size(); i++) {
	        if(players.get(i).id.equals(me)) return players.get(i);
        }
        return null;
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);

        for(int i=0; i<players.size(); i++) {
            Player p = players.get(i);
            font.draw(batch, p.name, 20, Gdx.graphics.getHeight() - (i*30));
        }

		batch.end();

        // get players
        sendRequest("players", "GET", null);
	}
	
	@Override
	public void dispose () {
		// remove
        sendRequest("player", "DELETE", null);

        batch.dispose();
        img.dispose();
	}

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
