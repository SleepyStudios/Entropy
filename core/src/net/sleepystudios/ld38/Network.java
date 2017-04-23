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

    public Network(LD38 game) {
        client = new Client(8192, 4096);
        //JOptionPane.showInputDialog(new JFrame(), "Choose a name")
        client.addListener(new Receiver(game, "x"));
        client.start();
        register();

        try {
            client.connect(10000, "localhost", 5000, 5001);
        } catch (IOException e) {
            e.printStackTrace();
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
    }
}
