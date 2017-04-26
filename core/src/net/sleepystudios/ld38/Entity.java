package net.sleepystudios.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import net.sleepystudios.ld38.particles.Smoke;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Entity {
    LD38 game;
    int id;
    float x, y;
    int type;

    float animSpeed = 0.1f, animTmr, scale;
    int fw = 16;
    int fh = 16;
    Animation anim, anim2;

    boolean exists = true;
    int waterLevel = 75;

    public Entity(LD38 game, int id, float x, float y, float scale, int type) {
        this.game = game;
        this.id = id;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.type = type;
    }

    boolean inited;
    public void initGraphics() {
        String filename = "";
        switch(type) {
            case 0:
                filename = "plant" + LD38.rand(1, 3);
                if(LD38.rand(0, 19)==0) filename = "plant4";
                if(LD38.rand(0, 49)==0) filename = "plant5";
                break;
            case 1:
                filename = "fire";
                break;
            case 2:
                filename = "water";
        }

        anim = new Animation(animSpeed, AnimGenerator.gen(filename + ".png", fw, fh));
        anim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        inited = true;
    }

    public void render(SpriteBatch batch) {
        if(!inited) initGraphics();

        animTmr += Gdx.graphics.getDeltaTime();

        TextureRegion tr = (TextureRegion) anim.getKeyFrame(animTmr, true);

        float r = ((float) waterLevel/40f);
        float g = ((float) waterLevel/75f);
        float b = ((float) waterLevel/75f);
        float min = 0.4f;
        if(r<min) r = min;
        if(g<min) g = min;
        if(b<min) b = min;

        Color colour = new Color(r, g, b, 1f);

        batch.setColor(colour);
        float sscale = scale;
        if(sscale>1f) sscale = 1f;
        batch.draw(tr, x, y, fw/2, fw/2, fw, fh, sscale, sscale, 0);
        batch.setColor(new Color(Color.WHITE));

        update();
    }

    float tmrScale;
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();

        tmrScale+=delta;
        float speed = 0.5f;
        if(!exists) speed=0.1f;
        if(tmrScale>=speed) {
            if(exists) {
                scale+=0.1f;
                if(scale>=10f) {
                    scale = 1f;
                    exists = false;
                }
            } else {
                scale-=0.1f;
                if(scale<=0) {
                    if(type==game.FIRE) game.particles.add(new Smoke(x, y));
                    game.entities.remove(this);
                    return;
                }
            }

            tmrScale = 0;
        }
    }
}
