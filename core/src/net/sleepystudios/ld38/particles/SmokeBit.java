package net.sleepystudios.ld38.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.sleepystudios.ld38.LD38;

/**
 * Created by Tudor on 23/04/2017.
 */
public class SmokeBit extends ParticleBit {
    private float maxVel = LD38.rand(0.4f, 1f);
    private float xVel, yVel;
    private float alphaSpeed = LD38.rand(0.02f, 0.04f);

    public SmokeBit(String folder, float x, float y) {
        super("particles/" + folder + "/smoke.png", x, y);

        xVel = LD38.randNoZero(-maxVel, maxVel);
        yVel = LD38.randNoZero(-maxVel, maxVel);
    }

    public void render(SpriteBatch batch) {
        super.handleRender();

        sprite.setX(sprite.getX()+xVel);
        sprite.setY(sprite.getY()+yVel);

        alpha+=(0-alpha)*alphaSpeed;
        sprite.setAlpha(alpha);
        sprite.draw(batch);

        if(alpha<(5/255f)) exists = false;
    }
}