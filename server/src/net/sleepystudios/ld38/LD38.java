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

    final int PLANT = 0, FIRE = 1;

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


    float tmrScale;
    public void update(float delta) {
        tmrScale+=delta;
        if(tmrScale>=1) {
            for(int i=0; i<entities.size(); i++) {
                entities.get(i).update();
            }
            tmrScale = 0;
        }
    }

    public static int rand(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public Player getPlayerByID(int id) {
        for(int i=0; i<players.size(); i++) {
            if(players.get(i).id==id) return players.get(i);
        }
        return null;
    }

    public Entity getEntityByID(String uuid) {
        for(int i=0; i<players.size(); i++) {
            if(entities.get(i).id.equals(uuid)) return entities.get(i);
        }
        return null;
    }

    private void register() {
        Kryo kryo = server.getKryo();
        kryo.register(Packets.Join.class);
        kryo.register(Packets.Leave.class);
        kryo.register(Packets.NewPlayer.class);
        kryo.register(Packets.Move.class);
        kryo.register(Packets.Entity.class);
        kryo.register(Packets.RemoveEntity.class);
    }

    public static void main(String[] args) {
        try {
            new LD38();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
