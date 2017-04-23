package net.sleepystudios.ld38;

import java.util.UUID;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Entity {
    LD38 game;
    String id;
    float x, y;
    int type;
    float scale;

    public Entity(LD38 game, float x, float y, int type) {
        this.game = game;
        id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public boolean collides(float x1, float y1, float x2, float y2) {
        int s = 16;

        if(x1 < x2 + s && x1 + s > x2 && y1 < y2 + s && s + y1 > y2) return true;
        return false;
    }

    public void update() {
        if(scale<1f) scale+=0.1f;

        if(type==game.FIRE && scale>=0.9f) {
            for(int i=0; i<game.entities.size(); i++) {
                Entity other = game.entities.get(i);

                if(other!=this) {
                    if(collides(x, y, other.x, other.y)) {
                        // destroy the plant
                        Packets.RemoveEntity re = new Packets.RemoveEntity();
                        re.id = other.id;
                        game.server.sendToAllTCP(re);
                        game.entities.remove(other);
                    }
                }
            }
        }

        boolean collides;
        if(scale>=0.9f) {
            for(int i=-1; i<2; i++) {
                for(int j=-1; j<2; j++) {
                    if(!(i==0 && j==0)) {
                        collides = false;

                        for(int k=0; k<game.entities.size(); k++) {
                            Entity other = game.entities.get(k);

                            if (!other.id.equals(id)) {
                                if (!collides(x + (i * 16), y + (j * 16), other.x, other.y)) {
                                    collides = true;
                                }
                            }
                        }

                        if(!collides) {
                            Entity e = new Entity(game, x + (i * 16), y + (j * 16), type);
                            game.entities.add(e);
                            System.out.println("spreading");

                            Packets.Entity ne = new Packets.Entity();
                            ne.id = e.id;
                            ne.x = e.x;
                            ne.y = e.y;
                            ne.scale = 0;
                            ne.type = e.type;
                            game.server.sendToAllTCP(ne);
                        }
                    }
                }
            }
        }
    }
}
