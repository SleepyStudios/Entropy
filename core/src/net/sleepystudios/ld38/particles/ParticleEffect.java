package net.sleepystudios.ld38.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Tudor on 23/04/2017.
 */
public class ParticleEffect {
    protected float x, y;
    protected int size;
    protected Array<ParticleBit> bits = new Array<ParticleBit>();

    public ParticleEffect() {}

    public ParticleEffect(float x, float y) {
        this.x = x;
        this.y = y;
        generate();
    }

    protected void generate() {}

    public void render(SpriteBatch batch) {
        for(int i=0; i<bits.size; i++) {
            if(bits.get(i).exists) {
                bits.get(i).render(batch);
            } else {
                bits.get(i).dispose();
                bits.removeIndex(i);
            }
        }
    }
}
