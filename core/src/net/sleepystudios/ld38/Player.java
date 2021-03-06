package net.sleepystudios.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.sleepystudios.ld38.particles.Action;

/**
 * Created by Tudor on 22/04/2017.
 */
public class Player {
    private LD38 game;
    int id, type;
    float x = Gdx.graphics.getWidth()/2, y = Gdx.graphics.getHeight()/2;

    float animSpeed = 0.1f, animTmr;
    int fw = 16;
    int fh = 16;
    Animation anim[] = new Animation[2];

    ShapeRenderer sr;

    boolean moving;
    int ai;

    public Player(LD38 game, int id, int type) {
        this.game = game;
        this.id = id;
        this.type = type;
    }

    boolean inited;
    public void initGraphics() {
        String filename = "";
        switch(type) {
            case LD38.PLANT:
                filename = "planter";
                break;
            case LD38.FIRE:
                filename = "firestarter";
                break;
            case LD38.WATER:
                filename = "waterer";
        }

        for(int i=0; i<anim.length; i++) {
            anim[i] = new Animation(animSpeed, AnimGenerator.gen(filename + i + ".png", fw, fh));
            anim[i].setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        }

        sr = new ShapeRenderer();

        inited = true;
    }

    public void render(SpriteBatch batch) {
        if(!inited) initGraphics();

        if(game.me==id) {
            game.cam.position.set(shownCamX+=(camX-shownCamX)*0.08f, shownCamY+=(camY-shownCamY)*0.1f, 0);
        }

        animTmr += Gdx.graphics.getDeltaTime();
        boolean shouldLoop = moving;
        if(!shouldLoop) animTmr = 0;

        TextureRegion tr = (TextureRegion) anim[ai].getKeyFrame(animTmr, shouldLoop);
        batch.draw(tr, x, y, fw/2, fw/2, fw, fh, 1f, 1f, 0);

        batch.end();
        if(!canAction) renderBar();
        batch.begin();

        update();
    }

    public void renderBar() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR);

        sr.setProjectionMatrix(game.cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        float width = 10;
        float height = 2;
        float xp = x+8-width/2;
        float yp = y-8;
        float perc = tmrAction / 1f * width;

        Color col = new Color(1,1,1,0.8f);
        switch(type) {
            case 0:
                col = new Color(0.1f, 0.6f, 0.1f, 0.8f);
                break;
            case 1:
                col = new Color(0.9f, 0.1f, 0.1f, 0.8f);
                break;
            case 2:
                col = new Color(0f, 0.5f, 1f, 0.8f);
                break;
        }

        sr.setColor(col);
        sr.rect(xp, yp, perc, height);

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    float tmrAction, tmrShakeScreen, tmrAtt;
    boolean canAction=true, shakeScreen, canAtt;
    public void update() {
        moving = false;

        if(game.me==id) {
            updateCam();

            float speed = 150*Gdx.graphics.getDeltaTime();

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if(y+speed<LD38.SCREEN_H-20) move(x, y+speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if(x-speed>2) move(x-speed, y);
                ai = 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if(y-speed>4) move(x, y-speed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if(x+speed<LD38.SCREEN_W-20) move(x+speed, y);
                ai = 0;
            }

            if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && canAction) {
                Packets.Entity e = new Packets.Entity();
                e.x = x;
                e.y = y;
                game.n.client.sendTCP(e);

                switch(type) {
                    case 0:
                        game.particles.add(new Action(x+8, y, "seed"));
                        game.playSound("plant");
                        break;
                    case 1:
                        game.particles.add(new Action(x+8, y, "fire"));
                        game.playSound("fire");
                        break;
                    case 2:
                        game.particles.add(new Action(x+8, y, "water"));
                        game.playSound("water");
                        break;
                }

                canAction = false;
                shakeScreen = true;
            }

            if(!canAction) tmrAction+=Gdx.graphics.getDeltaTime();
            if(tmrAction>=1) {
                canAction = true;
                tmrAction = 0;
            }

            if(shakeScreen) tmrShakeScreen+= Gdx.graphics.getDeltaTime();
            if(tmrShakeScreen>=0.2) {
                shakeScreen = false;
                tmrShakeScreen = 0;
            }

            if(!canAtt) tmrAtt+=Gdx.graphics.getDeltaTime();
            if(tmrAtt>=0.75) {
                canAtt = true;
                tmrAtt=0;
            }
        }
    }

    float shownCamX, shownCamY, camX, camY;
    boolean firstUpdate;
    public void updateCam() {
        // get the map properties to find the height/width, etc
        int w = LD38.SCREEN_W;
        int h = LD38.SCREEN_H;

        float minX = game.cam.zoom * (game.cam.viewportWidth / 2);
        float maxX = (w) - minX;
        float minY = game.cam.zoom * (game.cam.viewportHeight / 2);
        float maxY = (h) - minY;

        camX = Math.min(maxX, Math.max(x, minX));
        camY = Math.min(maxY, Math.max(y, minY));

        if(!firstUpdate) {
            shownCamX = camX;
            shownCamY = camY;
            game.cam.position.set(shownCamX, shownCamY, 0);

            firstUpdate = true;
        }

        float ox=0, oy=0;
        if(shakeScreen) {
            ox = game.rand(2, 4);
            oy = game.rand(2, 4);
        }

        game.cam.position.set(shownCamX+ox, shownCamY+oy, 0);
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
