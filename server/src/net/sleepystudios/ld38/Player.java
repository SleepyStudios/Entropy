package net.sleepystudios.ld38;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Player {
    LD38 game;
    int id, type=-1;
    float x = LD38.SCREEN_W/2, y = LD38.SCREEN_H/2;
    boolean ai;

    public Player(LD38 game, int id) {
        this.id = id;
        this.type = game.chooseType();
    }

    public Player(LD38 game, boolean ai) {
        this.game = game;
        this.id = -1-game.players.size();
        this.type = game.chooseType();
        this.ai = ai;

        x = LD38.rand(20, LD38.SCREEN_W-20);
        y = LD38.rand(20, LD38.SCREEN_H-20);
    }

    int nextX = -1, nextY = -1, dir;
    float tmrNextMove = 1;
    boolean moved;
    public void update(float delta) {
        float speed = 220*delta;

        if(nextX!=-1 && nextY!=-1) {
            if ((int) x >= nextX - 2 && (int) x <= nextX + 2) {
                x = nextX;
                moved = true;
            } else if (nextX > (int) x) {
                x += speed;
                moved = true;
                dir = 0;
            } else if (nextX < (int) x) {
                x -= speed;
                moved = true;
                dir = 1;
            }

            if ((int) y >= nextY - 2 && (int) y <= nextY + 2) {
                y = nextY;
                moved = true;
            } else if (nextY > (int) y) {
                y += speed;
                moved = true;
            } else if (nextY < (int) y) {
                y -= speed;
                moved = true;
            }

            if (nextX == (int) x && nextY == (int) y) {
                // place their given entity
                game.entities.add(new Entity(game, x, y, type));
                Packets.Entity e = new Packets.Entity();
                e.id = game.entities.get(game.entities.size()-1).id;
                e.type = game.entities.get(game.entities.size()-1).type;
                e.x = game.entities.get(game.entities.size()-1).x;
                e.y = game.entities.get(game.entities.size()-1).y;
                e.scale = game.entities.get(game.entities.size()-1).scale;
                game.server.sendToAllUDP(e);

                // send their particles
                Packets.AddParticles ap = new Packets.AddParticles();
                ap.type = type;
                ap.x = e.x;
                ap.y = e.y;
                game.server.sendToAllUDP(ap);

                nextX = -1;
                nextY = -1;
            }

            if(moved) sendMove();
        } else {
            switch (type) {
                case 0:
                    handlePlantNext(delta);
                    break;
                case 1:
                    handleFireNext(delta);
                    break;
                case 2:
                    handleWaterNext(delta);
            }
        }
    }

    public void handlePlantNext(float delta) {
        tmrNextMove+=delta;
        if(tmrNextMove>=2) {
            nextX = LD38.rand(20, LD38.SCREEN_W-20);
            nextY = LD38.rand(20, LD38.SCREEN_H-20);
            tmrNextMove = 0;
        }
    }

    public void handleFireNext(float delta) {
        tmrNextMove+=delta;
        if(tmrNextMove>=2) {
            // find a random plant
            if(game.entities.size()>0 && game.getCount(game.PLANT)>0) {
                int r = LD38.rand(0, game.entities.size()-1);
                while(game.entities.get(r)!=null && game.entities.get(r).type!=game.PLANT) {
                    r = LD38.rand(0, game.entities.size()-1);
                }

                nextX = (int) game.entities.get(r).x;
                nextY = (int) game.entities.get(r).y;
                tmrNextMove = 0;
            }
        }
    }

    public void handleWaterNext(float delta) {
        tmrNextMove+=delta;
        if(tmrNextMove>=1.5) {
            // choose between fire and plant
            int t = game.getCount(game.FIRE)>0 ? game.FIRE : game.PLANT;

            // find a random thing
            if(game.entities.size()>0 && game.getCount(t)>0) {
                int r = -1;

                HashMap<Integer, Integer> distances = new HashMap<Integer, Integer>();
                for(int i=0; i<game.entities.size(); i++) {
                    boolean canPut = true;
                    if(game.entities.get(i).type!=t) canPut = false;
                    if(t==LD38.FIRE) {
                        if(game.entities.get(i).scale<game.entities.get(i).extinguishThresh) canPut = false;
                    }

                    if(canPut) {
                        distances.put(i, getDistance(x, y, game.entities.get(i).x, game.entities.get(i).y));
                        if(r==-1 || distances.get(i)<distances.get(r)) r = i;
                    }
                }

                if(r>0 && r<game.entities.size() && game.entities.get(r)!=null) {
                    nextX = (int) game.entities.get(r).x;
                    nextY = (int) game.entities.get(r).y;
                    tmrNextMove = 0;
                }
            }
        }
    }

    private int getDistance(float x1, float y1, float x2, float y2) {
        return (int) Math.hypot(x1-x2, y1-y2);
    }

    public void sendMove() {
        Packets.Move m = new Packets.Move();
        m.id = id;
        m.ai = dir;
        m.x = x;
        m.y = y;
        game.server.sendToAllUDP(m);
        moved = false;
    }
}
