package net.sleepystudios.ld38;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Entity {
    LD38 game;
    int id;
    float x, y;
    int type;
    float scale;
    int waterLevel = 75;
    int maxChildren = 3;

    public Entity(LD38 game, float x, float y, int type) {
        game.idCount++;

        this.game = game;
        id = game.idCount;
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
            if(e.id!=id && e.type==type) if(collides(nx, ny, e.x, e.y, 8)) return true;
        }
        return false;
    }

    int children; boolean canSpread;
    float spread[] = {2f, 0.8f, 2f};
    float maxScale[] = {10f, 2f, 1f};
    float extinguishThresh = 0.5f;
    public void update() {
        scale+=0.1f;

        switch(type) {
            case LD38.PLANT:
                updatePlant();
                break;
            case LD38.FIRE:
                updateFire();
                break;
            case LD38.WATER:
                updateWater();
        }

        // spreading
        if(scale>=spread[type] && canSpread && children<maxChildren && game.getCount(game.PLANT)>0) {
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

        if(scale>=maxScale[type]) {
            // kill it off
            Packets.RemoveEntity re = new Packets.RemoveEntity();
            re.id = id;
            game.server.sendToAllUDP(re);
            game.entities.remove(this);
        }
    }

    private void updatePlant() {
        if(scale>=1f) {
            if (waterLevel >= 50) canSpread = true;
            if(game.rand(0, 9)==0) waterLevel-=2;
            sendWaterUpdate();
        }
    }

    private void updateFire() {
        // check if they can start burning any plants
        if(scale>=spread[LD38.FIRE]) {
            for(int i=0; i<game.entities.size(); i++) {
                Entity other = game.entities.get(i);

                if(other!=null && other!=this && other.type==game.PLANT) {
                    if(collides(x, y, other.x, other.y, 12)) {
                        // takeaway water
                        other.waterLevel -= 2;
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

    private void updateWater() {
        for (int i = 0; i < game.entities.size(); i++) {
            Entity other = game.entities.get(i);

            if (other != this) {
                if (other.type == LD38.PLANT) {
                    if (collides(x - 16, y - 16, other.x, other.y, 48)) {
                        // give it water
                        other.waterLevel += 30;
                        if (other.waterLevel > 100) other.waterLevel = 100;
                        other.sendWaterUpdate();
                    }
                } else if(other.type == LD38.FIRE) {
                    if (collides(x - 16, y - 16, other.x, other.y, 48) && other.scale>=extinguishThresh) {
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

    public void sendWaterUpdate() {
        Packets.WaterUpdate wu = new Packets.WaterUpdate();
        wu.id = id;
        wu.waterLevel = waterLevel;
        game.server.sendToAllUDP(wu);
    }
}
