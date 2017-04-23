package net.sleepystudios.ld38;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Receiver extends Listener {
    LD38 game;
    String name;

    public Receiver(LD38 game, String name) {
        this.game = game;
        this.name = name;
    }

    @Override
    public void connected(Connection c) {
        super.connected(c);

        Packets.Join j = new Packets.Join();
        j.name = name;
        c.sendTCP(j);
    }

    @Override
    public void received(Connection c, Object o) {
        super.received(c, o);

        if(o instanceof Packets.Join) {
            String name = ((Packets.Join) o).name;
            int id = c.getID();

            game.me = id;
            game.players.add(new Player(game, name, id));
        }

        if(o instanceof Packets.Leave) {
            int id = ((Packets.Leave) o).id;
            game.players.remove(game.getPlayerByID(id));
        }

        if(o instanceof Packets.NewPlayer) {
            Packets.NewPlayer np = ((Packets.NewPlayer) o);

            game.players.add(new Player(game, np.name, np.id));
            if(np.x!=0) game.getPlayerByID(np.id).x = np.x;
            if(np.y!=0) game.getPlayerByID(np.id).y = np.y;
        }

        if(o instanceof Packets.Move) {
            Packets.Move m = ((Packets.Move) o);

            game.getPlayerByID(m.id).x = m.x;
            game.getPlayerByID(m.id).y = m.y;
        }

        if(o instanceof Packets.Entity) {
            Packets.Entity e = ((Packets.Entity) o);
            game.entities.add(new Entity(e.id, e.x, e.y, e.scale, e.type));
        }

        if(o instanceof Packets.RemoveEntity) {
            String uuid = ((Packets.RemoveEntity) o).id;
            game.entities.remove(game.getEntityByID(uuid));
        }
    }

    @Override
    public void disconnected(Connection c) {
        super.disconnected(c);
    }
}
