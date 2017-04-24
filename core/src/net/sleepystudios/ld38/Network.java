package net.sleepystudios.ld38;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Network {
    public Client client;

    public Network(LD38 game, String ip) {
        game.players.clear();
        game.entities.clear();
        game.particles.clear();
        game.actionMessages.clear();
        game.exclams.clear();

        client = new Client(8192, 4096);
        client.addListener(new Receiver(game));
        client.start();
        register();

        try { //35.156.58.36
            client.connect(10000, ip, 5000, 5001);
        } catch (IOException e) {
            System.out.println("Couldn't connect!");
        }
    }

    private void register() {
        Kryo kryo = client.getKryo();
        kryo.register(Packets.Join.class);
        kryo.register(Packets.Leave.class);
        kryo.register(Packets.NewPlayer.class);
        kryo.register(Packets.Move.class);
        kryo.register(Packets.Entity.class);
        kryo.register(Packets.RemoveEntity.class);
        kryo.register(Packets.AddParticles.class);
        kryo.register(Packets.WaterUpdate.class);
        kryo.register(Packets.Attention.class);
    }
}
