package net.sleepystudios.ld38.particles;

import net.sleepystudios.ld38.LD38;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Smoke extends ParticleEffect {
    public Smoke(float x, float y) {
        super(x, y);
    }

    @Override
    protected void generate() {
        size = LD38.rand(1, 1);

        for(int i=0; i<size; i++) {
            bits.add(new SmokeBit("smoke", x, y));
        }
    }
}
