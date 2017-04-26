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
    }

    public String getRole(Player p) {
        if(p.type==game.PLANT) return "planter";
        if(p.type==game.FIRE) return "firestarter";
        return "waterer";
    }

    @Override
    public void received(Connection c, Object o) {
        super.received(c, o);

        if(o instanceof Packets.Join) {
            // make AIs
            if(game.players.size()==0) {
                for(int i=0; i<game.botNum; i++) game.players.add(new Player(game, true));
            }

            // add them
            game.players.add(new Player(game, c.getID()));

            int type = game.getPlayerByID(c.getID()).type;
            ((Packets.Join) o).type = type;
            c.sendTCP(o);

            String role = getRole(game.getPlayerByID(c.getID()));
            System.out.println("player joined as a " + role + "!");

            // send them to everyone else
            Packets.NewPlayer np = new Packets.NewPlayer();
            np.type = type;
            np.id = c.getID();
            game.server.sendToAllExceptTCP(c.getID(), np);

            // send them everyone else
            for(int i=0; i<game.players.size(); i++) {
                Player p = game.players.get(i);
                if(p.id!=c.getID()) {
                    np = new Packets.NewPlayer();
                    np.type = p.type;
                    np.id = p.id;
                    np.x = p.x;
                    np.y = p.y;
                    c.sendTCP(np);
                }
            }

            // send them entities
//            for(int i=0; i<game.entities.size(); i++) {
//                Entity e = game.entities.get(i);
//
//                Packets.Entity ne = new Packets.Entity();
//                ne.id = e.id;
//                ne.x = e.x;
//                ne.y = e.y;
//                ne.scale = e.scale;
//                ne.type = e.type;
//                c.sendUDP(ne);
//
//                if(e.type==game.PLANT) e.sendWaterUpdateTo(c.getID());
//            }
        }

        if(o instanceof Packets.Move) {
            Packets.Move m = ((Packets.Move) o);
            Player p = game.getPlayerByID(c.getID());
            if(p==null) return;

            p.x = m.x;
            p.y = m.y;
            game.server.sendToAllExceptUDP(c.getID(), o);
        }

        if(o instanceof Packets.Entity) {
            Packets.Entity e = ((Packets.Entity) o);

            int type = game.getPlayerByID(c.getID()).type;
            game.entities.add(new Entity(game, e.x, e.y, type));
            e.id = game.entities.get(game.entities.size()-1).id;
            e.type = game.entities.get(game.entities.size()-1).type;
            game.server.sendToAllUDP(e);

            Packets.AddParticles ap = new Packets.AddParticles();
            ap.type = type;
            ap.x = e.x;
            ap.y = e.y;
            game.server.sendToAllExceptUDP(c.getID(), ap);
        }

        if(o instanceof Packets.RemoveEntity) {
            c.sendUDP(o);
        }

        if(o instanceof Packets.WaterUpdate) {
            Packets.WaterUpdate wu = ((Packets.WaterUpdate) o);
            Entity e = game.getEntityByID(wu.id);
            if(e!=null) {
                Packets.Entity ne = new Packets.Entity();
                ne.id = e.id;
                ne.x = e.x;
                ne.y = e.y;
                ne.scale = e.scale;
                ne.type = e.type;
                c.sendUDP(ne);
            }
            c.sendUDP(o);
        }

        if(o instanceof Packets.Attention) {
            game.server.sendToAllExceptUDP(c.getID(), o);
        }
    }

    @Override
    public void disconnected(Connection c) {
        super.disconnected(c);

        Player p = game.getPlayerByID(c.getID());
        if(p!=null) {
            System.out.println("player left!");

            Packets.Leave l = new Packets.Leave();
            l.id = p.id;
            game.server.sendToAllTCP(l);
            game.players.remove(game.getPlayerByID(c.getID()));
        }

        if(game.players.size()==game.botNum) {
            game.players.clear();
        }
    }
}
