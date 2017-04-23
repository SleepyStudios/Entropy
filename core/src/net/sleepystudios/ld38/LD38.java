package net.sleepystudios.ld38;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class LD38 extends ApplicationAdapter implements ActionListener, InputProcessor {
	SpriteBatch batch;
	Texture img;
    BitmapFont font;
    Network n;
    OrthographicCamera c;
    int me = -1;
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Entity> entities = new ArrayList<Entity>();

    final int PLANT = 0, FIRE = 1;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("background.bmp");
		font = new BitmapFont();

        n = new Network(this);
        c = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.input.setInputProcessor(this);
	}

    public static int rand(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

//    public void updateCam(float x, float y) {
//        int mapPixelWidth = Gdx.graphics.getWidth();
//        int mapPixelHeight = Gdx.graphics.getHeight();
//
//        float minCameraX = c.zoom * (c.viewportWidth / 2);
//        float maxCameraX = (mapPixelWidth) - minCameraX;
//        float minCameraY = c.zoom * (c.viewportHeight / 2);
//        float maxCameraY = (mapPixelHeight) - minCameraY;
//
//        c.position.set(Math.min(maxCameraX, Math.max(x, minCameraX)), Math.min(maxCameraY, Math.max(y, minCameraY)), 0);
//    }

    public Player getMe() {
	    for(int i=0; i<players.size(); i++) {
	        if(players.get(i).id==me) return players.get(i);
        }
        return null;
    }

    public Player getPlayerByID(int id) {
        for(int i=0; i<players.size(); i++) {
            if(players.get(i).id==id) return players.get(i);
        }
        return null;
    }

    public Entity getEntityByID(String uuid) {
        for(int i=0; i<entities.size(); i++) {
            if(entities.get(i).id.equals(uuid)) return entities.get(i);
        }
        return null;
    }

    public int getCount(int type) {
        int c = 0;
        for(int i=0; i<entities.size(); i++) {
            if(entities.get(i)!=null) entities.get(i).type==type) c++;
        }
        return c;
    }

	@Override
	public void render () {
        if(me==-1) return;

		Gdx.gl.glClearColor(72/255f, 49/255f, 40/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        c.update();

        batch.setProjectionMatrix(c.combined);
		batch.begin();

		batch.draw(img, 0, 0);

        for(int i=0; i<entities.size(); i++) {
            Entity e = entities.get(i);
            e.render(batch);
        }

        for(int i=0; i<players.size(); i++) {
            Player p = players.get(i);
            p.render(batch);
        }

        font.draw(batch, "Treehuggers: " + getCount(PLANT), 10, Gdx.graphics.getHeight()-12);
        font.draw(batch, "Pyromaniacs: " + getCount(FIRE), 10, Gdx.graphics.getHeight()-32);

		batch.end();
	}
	
	@Override
	public void dispose () {
        batch.dispose();
        img.dispose();
	}

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
	    if(keycode==Input.Keys.E) {
            Packets.Entity e = new Packets.Entity();
            e.x = getMe().x;
            e.y = getMe().y;
            n.client.sendTCP(e);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount>0) {
            c.zoom = 1;
        } else {
            c.zoom = 0.5f;
        }
        return false;
    }
}
