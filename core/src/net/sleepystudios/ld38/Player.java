package net.sleepystudios.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Tudor on 22/04/2017.
 */
public class Player {
    private LD38 game;
    String name;
    int id, type;
    float x = Gdx.graphics.getWidth()/2, y = Gdx.graphics.getHeight()/2;

    float animSpeed = 0.1f, animTmr;
    int fw = 16;
    int fh = 16;
    Animation anim[] = new Animation[2];

    boolean moving;
    int ai;

    public Player(LD38 game, String name, int id) {
        this.game = game;
        this.name = name;
        this.id = id;
    }

    boolean inited;
    public void initGraphics() {
        for(int i=0; i<anim.length; i++) {
            anim[i] = new Animation(animSpeed, AnimGenerator.gen("player" + i + ".png", fw, fh));
            anim[i].setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        }

        inited = true;
    }

    public void render(SpriteBatch batch) {
        if(!inited) initGraphics();

        if(game.me==id) {
            game.c.position.set(shownCamX+=(camX-shownCamX)*0.08f, shownCamY+=(camY-shownCamY)*0.08f, 0);
        }

        animTmr += Gdx.graphics.getDeltaTime();
        boolean shouldLoop = moving;
        if(!shouldLoop) animTmr = 0;

        TextureRegion tr = (TextureRegion) anim[ai].getKeyFrame(animTmr, shouldLoop);
        batch.draw(tr, x, y, fw/2, fw/2, fw, fh, 1f, 1f, 0);

        update();
    }

    float tmrAction;
    boolean canAction;
    public void update() {
        moving = false;

        if(game.me==id) {
            updateCam();

            float speed = 150*Gdx.graphics.getDeltaTime();

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if(y+speed<Gdx.graphics.getHeight()-20) move(x, y+speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if(x-speed>2) move(x-speed, y);
                ai = 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if(y-speed>4) move(x, y-speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if(x+speed<Gdx.graphics.getWidth()-20) move(x+speed, y);
                ai = 0;
            }

            if(!canAction) tmrAction+=Gdx.graphics.getDeltaTime();
            if(tmrAction>=1) {
                canAction = true;
                tmrAction = 0;
            }
        }
    }

    float shownCamX, shownCamY, camX, camY;
    boolean firstUpdate;
    public void updateCam() {
        // get the map properties to find the height/width, etc
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        float minX = game.c.zoom * (game.c.viewportWidth / 2);
        float maxX = (w) - minX;
        float minY = game.c.zoom * (game.c.viewportHeight / 2);
        float maxY = (h) - minY;

        camX = Math.min(maxX, Math.max(x, minX));
        camY = Math.min(maxY, Math.max(y, minY));

        if(!firstUpdate) {
            shownCamX = camX;
            shownCamY = camY;
            game.c.position.set(shownCamX, shownCamY, 0);

            firstUpdate = true;
        }
    }

    private void move(float x, float y) {
        this.x = x;
        this.y = y;

        moving = true;

        Packets.Move m = new Packets.Move();
        m.id = id;
        m.ai = ai;
        m.x = x;
        m.y = y;
        game.n.client.sendUDP(m);
    }
}
