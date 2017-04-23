package net.sleepystudios.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Entity {
    String id;
    float x, y;
    int type;

    float animSpeed = 0.1f, animTmr, scale;
    int fw = 16;
    int fh = 16;
    Animation anim;

    public Entity(String id, float x, float y, float scale, int type) {
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
                break;
            case 1:
                filename = "fire";
        }

        anim = new Animation(animSpeed, AnimGenerator.gen(filename + ".png", fw, fh));
        anim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        inited = true;
    }

    public void render(SpriteBatch batch) {
        if(!inited) initGraphics();

        animTmr += Gdx.graphics.getDeltaTime();

        TextureRegion tr = (TextureRegion) anim.getKeyFrame(animTmr, true);
        batch.draw(tr, x, y, fw/2, fw/2, fw, fh, scale, scale, 0);

        update();
    }

    float tmrScale;
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();

        tmrScale+=delta;
        if(tmrScale>=1.0 && scale<1f) {
            scale+=0.1f;
            tmrScale = 0;
        }
    }
}
