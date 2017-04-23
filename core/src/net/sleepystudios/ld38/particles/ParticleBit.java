package net.sleepystudios.ld38.particles;

/**
 * Created by Tudor on 23/04/2017.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.sleepystudios.ld38.LD38;

public class ParticleBit {
    String texName;
    float x, y, alpha = 1f, tmrLife;
    public Sprite sprite;
    public boolean exists = true;

    public ParticleBit(String texName, float x, float y) {
        this.texName = texName;
        this.x = x;
        this.y = y;
    }

    public boolean texInited;

    public void initGraphics() {
        sprite = new Sprite(new Texture(texName));
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
        sprite.setPosition(x, y);

        float angle = LD38.rand(0, 360);
        sprite.setRotation(angle);
        texInited = true;
    }

    public void handleRender() {
        if (!texInited) initGraphics();
        tmrLife += Gdx.graphics.getDeltaTime();
    }

    public void render(SpriteBatch batch) {
        handleRender();

        if (tmrLife >= 45) alpha += (0 - alpha) * 0.002f;
        sprite.setColor(1f, 1f, 1f, alpha);
        sprite.draw(batch);

        if (alpha < (5 / 255f)) exists = false;
    }

    public void dispose() {
        sprite.getTexture().dispose();
    }
}