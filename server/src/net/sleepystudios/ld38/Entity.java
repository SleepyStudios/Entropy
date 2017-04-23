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

    public boolean collides(float x1, float y1, float x2, float y2, int s) {
        if(x1 < x2 + s && x1 + s > x2 && y1 < y2 + s && s + y1 > y2) return true;
        return false;
    }

    public boolean collidesType(float nx, float ny) {
        for(int i=0; i<game.entities.size(); i++) {
            Entity e = game.entities.get(i);
            if(!e.id.equals(id) && e.type==type) {
                if(collides(nx, ny, e.x, e.y, 8)) return true;
            }
        }
        return false;
    }

    int children;
    public void update() {
        scale+=0.1f;

        if(type==game.FIRE && scale>=0.9f) {
            for(int i=0; i<game.entities.size(); i++) {
                Entity other = game.entities.get(i);

                if(other!=this && other.type==game.PLANT) {
                    if(collides(x, y, other.x, other.y, 12)) {
                        // destroy the plant
                        Packets.RemoveEntity re = new Packets.RemoveEntity();
                        re.id = other.id;
                        game.server.sendToAllTCP(re);
                        game.entities.remove(other);
                    }
                }
            }
        }

        if(scale>=2f && children<3 && game.getPlantCount()>0) {
            int offset = 24;
            float nx = x + game.rand(-offset, offset);
            float ny = y + game.rand(-offset, offset);

            if(!collidesType(nx, ny) && nx>0 && nx<640 && ny>0 && ny<480) {
                Entity e = new Entity(game, nx, ny, type);
                game.entities.add(e);

                Packets.Entity ne = new Packets.Entity();
                ne.id = e.id;
                ne.x = e.x;
                ne.y = e.y;
                ne.scale = 0;
                ne.type = e.type;
                game.server.sendToAllTCP(ne);
                children++;
            }
        }

        if(scale>=8f) {
            // kill it off
            Packets.RemoveEntity re = new Packets.RemoveEntity();
            re.id = id;
            game.server.sendToAllTCP(re);
            game.entities.remove(this);
        }
    }
}
