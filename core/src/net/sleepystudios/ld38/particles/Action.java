package net.sleepystudios.ld38.particles;

import net.sleepystudios.ld38.LD38;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Action extends ParticleEffect {
    String folder = "";

    public Action(float x, float y, String folder) {
        this.x = x;
        this.y = y;
        this.folder = folder;
        generate();
    }

    @Override
    protected void generate() {
        size = LD38.rand(3, 5);
        if(folder.equals("water")) size = LD38.rand(12, 15);
        if(folder.equals("water")) size = LD38.rand(20, 25);

        for(int i=0; i<size; i++) {
            bits.add(new ActionBit(this.folder, x, y));
        }
    }
}