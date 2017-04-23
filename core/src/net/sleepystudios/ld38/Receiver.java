package net.sleepystudios.ld38;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by Tudor on 23/04/2017.
 */
public class Receiver extends Listener {
    LD38 game;

    public Receiver(LD38 game) {
        this.game = game;
    }

    @Override
    public void connected(Connection c) {
        super.connected(c);

        Packets.Join j = new Packets.Join();
        c.sendTCP(j);
    }

    @Override
    public void received(Connection c, Object o) {
        super.received(c, o);

        if(o instanceof Packets.Join) {
            int id = c.getID();
            int type = ((Packets.Join) o).type;

            game.me = id;
            game.players.add(new Player(game, id, type));
        }

        if(o instanceof Packets.Leave) {
            int id = ((Packets.Leave) o).id;
            Player p = game.getPlayerByID(id);
            if(p==null) return;

            game.players.remove(p);
        }

        if(o instanceof Packets.NewPlayer) {
            Packets.NewPlayer np = ((Packets.NewPlayer) o);

            game.players.add(new Player(game, np.id, np.type));
            if(np.x!=0) game.getPlayerByID(np.id).x = np.x;
            if(np.y!=0) game.getPlayerByID(np.id).y = np.y;
        }

        if(o instanceof Packets.Move) {
            Packets.Move m = ((Packets.Move) o);
            Player p = game.getPlayerByID(m.id);
            if(p==null) return;

            p.x = m.x;
            p.y = m.y;
            p.ai = m.ai;
            if(m.id!=game.me) p.moving = true;
        }

        if(o instanceof Packets.Entity) {
            Packets.Entity e = ((Packets.Entity) o);
            game.entities.add(new Entity(game, e.id, e.x, e.y, e.scale, e.type));
        }

        if(o instanceof Packets.RemoveEntity) {
            String uuid = ((Packets.RemoveEntity) o).id;
            Entity e = game.getEntityByID(uuid);
            if(e!=null) e.exists = false;
        }

        if(o instanceof Packets.AddParticles) {
            Packets.AddParticles ap = ((Packets.AddParticles) o);
            game.queueParticles = ap.type;
            game.queuePX = ap.x;
            game.queuePY = ap.y;
        }
    }

    @Override
    public void disconnected(Connection c) {
        super.disconnected(c);
        game.me = -1;
    }
}
