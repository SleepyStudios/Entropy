package net.sleepystudios.ld38;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Tudor on 24/04/2017.
 */
public class Exclam {
    float x, y, tar;
    Sprite e;

    public Exclam(float x, float y) {
        this.x = x;
        this.y = y;
        tar = y+25;
    }

    private boolean inited;
    public void initGraphics() {
        e = new Sprite(new Texture("exclam.png"));
        e.setPosition(x, y);
        inited = true;
    }

    public void render(SpriteBatch batch) {
        if(!inited) initGraphics();

        e.setY(e.getY()+(tar-e.getY())*0.05f);
        e.setAlpha(e.getColor().a+(0-e.getColor().a)*0.03f);
        e.draw(batch);
    }
}
