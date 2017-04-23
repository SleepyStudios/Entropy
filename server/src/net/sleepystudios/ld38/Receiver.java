package net.sleepystudios.ld38;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.bouncycastle.util.Pack;

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
    }

    public String getRole(Player p) {
        if(p.type==0) return "planter";
        return "firestarter";
    }

    @Override
    public void received(Connection c, Object o) {
        super.received(c, o);

        if(o instanceof Packets.Join) {
            String name = ((Packets.Join) o).name;
            c.sendTCP(o);

            // add them
            game.players.add(new Player(name, c.getID()));

            String role = getRole(game.getPlayerByID(c.getID()));
            System.out.println(name + " joined as a " + role + "!");

            // send them to everyone else
            Packets.NewPlayer np = new Packets.NewPlayer();
            np.name = name;
            np.id = c.getID();
            game.server.sendToAllExceptTCP(c.getID(), np);

            // send them everyone else
            for(int i=0; i<game.players.size(); i++) {
                Player p = game.players.get(i);
                if(p.id!=c.getID()) {
                    np = new Packets.NewPlayer();
                    np.name = p.name;
                    np.id = p.id;
                    np.x = p.x;
                    np.y = p.y;
                    c.sendTCP(np);
                }
            }

            // send them entities
            for(int i=0; i<game.entities.size(); i++) {
                Entity e = game.entities.get(i);

                Packets.Entity ne = new Packets.Entity();
                ne.id = e.id;
                ne.x = e.x;
                ne.y = e.y;
                ne.scale = e.scale;
                ne.type = e.type;
                c.sendTCP(ne);
            }
        }

        if(o instanceof Packets.Move) {
            Packets.Move m = ((Packets.Move) o);

            game.getPlayerByID(c.getID()).x = m.x;
            game.getPlayerByID(c.getID()).y = m.y;
            game.server.sendToAllExceptUDP(c.getID(), o);
        }

        if(o instanceof Packets.Entity) {
            Packets.Entity e = ((Packets.Entity) o);

            int type = game.getPlayerByID(c.getID()).type;
            game.entities.add(new Entity(game, e.x, e.y, type));
            e.id = game.entities.get(game.entities.size()-1).id;
            e.type = game.entities.get(game.entities.size()-1).type;
            game.server.sendToAllTCP(e);
        }
    }

    @Override
    public void disconnected(Connection c) {
        super.disconnected(c);

        Player p = game.getPlayerByID(c.getID());
        if(p!=null) {
            System.out.println(p.name + " left!");

            Packets.Leave l = new Packets.Leave();
            l.id = p.id;
            game.server.sendToAllTCP(l);
            game.players.remove(game.getPlayerByID(c.getID()));
        }
    }
}
