package net.sleepystudios.ld38;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tudor on 23/04/2017.
 */
public class LD38 {
    Server server;
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Entity> entities = new ArrayList<Entity>();

    final int PLANT = 0, FIRE = 1, WATER = 2;

    public LD38() throws Exception {
        server = new Server(8192, 4096);
        server.bind(5000, 5001);
        server.addListener(new Receiver(this));
        server.start();
        register();

        System.out.println("Server running");

        // main loop
        loop();
    }

    public void loop() {
        boolean running = true;

        double ns = 1000000000.0 / 60.0;
        float delta = 0;

        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                update(delta/60f);
                delta--;
            }
        }
    }


    float tmrScale, tmrStats;
    public void update(float delta) {
        tmrScale+=delta;
        if(tmrScale>=0.5 && players.size()>0) {
            for(int i=0; i<entities.size(); i++) {
                entities.get(i).update();
            }
            tmrScale = 0;
        }

        tmrStats+=delta;
        if(tmrStats>=30) {
            if(players.size()>0) System.out.println("[STATS] " + players.size() + " players online");
            if(entities.size()>0) System.out.println("[STATS] " + entities.size() + " entities");
            tmrStats = 0;
        }

        for(int i=0; i<players.size(); i++) {
            if(players.get(i).ai) players.get(i).update(delta);
        }
    }

    public static int rand(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public int chooseType() {
        if(players.size()==0) {
            return rand(0, 2);
        } else {
            int counts[] = new int[3];

            for(int i=0; i<players.size(); i++) {
                if(players.get(i).type!=-1) {
                    counts[players.get(i).type]++;
                }
            }

            if(counts[0]==0) return 0;
            if(counts[0]<counts[1] && counts[0]<counts[2]) return 0;
            if(counts[1]<counts[0] && counts[1]<counts[2]) return 1;
            if(counts[2]<counts[0] && counts[2]<counts[1]) return 2;
            return rand(0, 2);
        }
    }

    public Player getPlayerByID(int id) {
        for(int i=0; i<players.size(); i++) {
            if(players.get(i).id==id) return players.get(i);
        }
        return null;
    }

    public Entity getEntityByID(String uuid) {
        for(int i=0; i<entities.size(); i++) {
            if(entities.get(i).id.equals(uuid)) return entities.get(i);
        }
        return null;
    }

    public int getCount(int t) {
        int c = 0;
        for(int i=0; i<entities.size(); i++) {
            if(entities.get(i).type==t) c++;
        }
        return c;
    }

    private void register() {
        Kryo kryo = server.getKryo();
        kryo.register(Packets.Join.class);
        kryo.register(Packets.Leave.class);
        kryo.register(Packets.NewPlayer.class);
        kryo.register(Packets.Move.class);
        kryo.register(Packets.Entity.class);
        kryo.register(Packets.RemoveEntity.class);
        kryo.register(Packets.AddParticles.class);
        kryo.register(Packets.WaterUpdate.class);
    }

    public static void main(String[] args) {
        try {
            new LD38();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
