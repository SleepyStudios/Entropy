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
    int waterLevel = 75;
    int maxChildren = 3;

    public Entity(LD38 game, float x, float y, int type) {
        this.game = game;
        id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.type = type;
        if(type==game.FIRE) maxChildren = game.rand(1, 3);
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

    int children; boolean canSpread;
    public void update() {
        scale+=0.1f;

        if(type==game.PLANT && scale>=1f) {
            if(game.rand(0, 9)==0) waterLevel-=2;
            sendWaterUpdate();
        }

        if(type==game.WATER) {
            for (int i = 0; i < game.entities.size(); i++) {
                Entity other = game.entities.get(i);

                if (other != this) {
                    if (other.type == game.PLANT) {
                        if (collides(x - 16, y - 16, other.x, other.y, 48)) {
                            // give it water
                            other.waterLevel += 30;
                            if (other.waterLevel > 100) other.waterLevel = 100;
                            other.sendWaterUpdate();
                        }
                    } else if(other.type == game.FIRE) {
                        if (collides(x - 24, y - 24, other.x, other.y, 64) && other.scale>=1.5f) {
                            // destroy the fire
                            Packets.RemoveEntity re = new Packets.RemoveEntity();
                            re.id = other.id;
                            game.server.sendToAllUDP(re);
                            game.entities.remove(other);
                        }
                    }
                }
            }

            // destroy this too
            Packets.RemoveEntity re = new Packets.RemoveEntity();
            re.id = id;
            game.server.sendToAllUDP(re);
            game.entities.remove(this);
        }

        if(scale>=1f) {
            // plant
            if(type==game.PLANT) {
                if(waterLevel>=50) canSpread = true;
            }

            // fire
            if(type==game.FIRE) {
                for(int i=0; i<game.entities.size(); i++) {
                    Entity other = game.entities.get(i);

                    if(other!=null && other!=this && other.type==game.PLANT) {
                        if(collides(x, y, other.x, other.y, 12)) {
                            // takeaway water
                            other.waterLevel -= 5;
                            other.sendWaterUpdate();

                            if(other.waterLevel<=0) {
                                // destroy the plant
                                Packets.RemoveEntity re = new Packets.RemoveEntity();
                                re.id = other.id;
                                game.server.sendToAllUDP(re);
                                game.entities.remove(other);
                            }

                            canSpread = true;
                        }
                    }
                }
            }
        }

        // spreading
        if(scale>=2f && canSpread && children<maxChildren && game.getCount(game.PLANT)>0) {
            int offset = 24;
            float nx = x + game.rand(-offset, offset);
            float ny = y + game.rand(-offset, offset);

            if(!collidesType(nx, ny) && nx>0 && nx<640 && ny>0 && ny<480) {
                Entity e = new Entity(game, nx, ny, type);
                if(type==game.PLANT) e.waterLevel = waterLevel-5;
                game.entities.add(e);

                Packets.Entity ne = new Packets.Entity();
                ne.id = e.id;
                ne.x = e.x;
                ne.y = e.y;
                ne.scale = 0;
                ne.type = e.type;
                game.server.sendToAllUDP(ne);

                // send a water update
                if(type==game.PLANT) game.getEntityByID(e.id).sendWaterUpdate();

                children++;
            }
        }

        float maxScale = 0;
        if(type==game.PLANT) {
            if(waterLevel>5) maxScale = 10f;
        }
        if(type==game.FIRE) maxScale = 5f;
        if(scale>=maxScale) {
            // kill it off
            Packets.RemoveEntity re = new Packets.RemoveEntity();
            re.id = id;
            game.server.sendToAllUDP(re);
            game.entities.remove(this);
        }
    }

    public void sendWaterUpdate() {
        Packets.WaterUpdate wu = new Packets.WaterUpdate();
        wu.id = id;
        wu.waterLevel = waterLevel;
        game.server.sendToAllUDP(wu);
    }

    public void sendWaterUpdateTo(int c) {
        Packets.WaterUpdate wu = new Packets.WaterUpdate();
        wu.id = id;
        wu.waterLevel = waterLevel;
        game.server.sendToUDP(c, wu);
    }
}
