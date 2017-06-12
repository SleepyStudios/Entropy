package net.sleepystudios.ld38;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.sleepystudios.ld38.particles.ParticleEffect;
import net.sleepystudios.ld38.particles.Action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class LD38 extends ApplicationAdapter implements ActionListener, InputProcessor {
	SpriteBatch batch;
	Texture img;
    BitmapFont font;
    Network n;
    OrthographicCamera cam;
    ShapeRenderer sr;

    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Entity> entities = new ArrayList<Entity>();
    ArrayList<ParticleEffect> particles = new ArrayList<ParticleEffect>();

    int queueParticles = -1;
    float queuePX, queuePY;

    ArrayList<ActionMessage> actionMessages = new ArrayList<ActionMessage>();
    ArrayList<Exclam> exclams = new ArrayList<Exclam>();
    float tmrMessages; int messageNum;
    String ip = ""; int tcp = 5000, udp = 5001;

    public static final int PLANT = 0, FIRE = 1, WATER = 2;
    public static final int SCREEN_W = 640, SCREEN_H = 480;
    int me = -1;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("background.bmp");
		font = new BitmapFont();

		try {
		    readConfig();
        } catch (Exception e) {
		    e.printStackTrace();
        }

        n = new Network(this, ip, tcp, udp);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr = new ShapeRenderer();

        Music music = Gdx.audio.newMusic(Gdx.files.internal("vibrations.mp3"));
        music.setVolume(0.8f);
        music.setLooping(true);
        music.play();

        Gdx.input.setInputProcessor(this);
	}

	public void readConfig() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("config.dat"));
        try {
            String line;
            while((line = br.readLine()) != null) {
                if(!line.startsWith("#")) {
                    // ip
                    if (line.startsWith("ip")) {
                        if (line.split(":")[1].contains("auto")) {
                            ip = getMasterIP();
                        } else {
                            ip = line.split(":")[1];
                        }
                    }

                    // tcp
                    if (line.startsWith("tcp")) {
                        tcp = Integer.valueOf(line.split(":")[1]);
                    }

                    // udp
                    if (line.startsWith("udp")) {
                        udp = Integer.valueOf(line.split(":")[1]);
                    }
                }
            }
        } finally {
            br.close();
        }
    }

    public String getMasterIP() throws IOException {
        String url = "http://sleepystudios.net/entropyip.txt";
        URL urlObj = new URL(url);
        URLConnection lu = urlObj.openConnection();

        // get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader(lu.getInputStream()));
        return rd.readLine();
    }

    // generates a random number
    public static int rand(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
    public static float rand(float min, float max) {
        return min + new Random().nextFloat() * (max - min);
    }

    // random number that cannot be 0
    public static float randNoZero(float min, float max) {
        float r = rand(min, max);
        return r != 0 ? r : randNoZero(min, max);
    }

    // sound
    public static void playSound(String s) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(s + ".wav"));
        sound.play(1f);
    }

    public Player getMe() {
	    for(int i=0; i<players.size(); i++) {
	        if(players.get(i).id==me) return players.get(i);
        }
        return null;
    }

    public Player getPlayerByID(int id) {
        for(int i=0; i<players.size(); i++) {
            if(players.get(i).id==id) return players.get(i);
        }
        return null;
    }

    public Entity getEntityByID(int id) {
        for(int i=0; i<entities.size(); i++) {
            if(entities.get(i)!=null && entities.get(i).id==id) return entities.get(i);
        }
        return null;
    }

    public int getCount(int type) {
        int count = 0;
        for(int i=0; i<entities.size(); i++) {
            if(entities.get(i)!=null && entities.get(i).type==type) count++;
        }
        return count;
    }

	@Override
	public void render () {
        Gdx.gl.glClearColor(72/255f, 49/255f, 40/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(me==-1) {
            batch.begin();
            font.draw(batch, "Couldn't connect, press 'space' to try again", 10, Gdx.graphics.getHeight()-10);
            font.draw(batch, "You can also try tweeting @sekaru_ or @sleepystudios to let us know the server is down", 10, Gdx.graphics.getHeight()-30);
            batch.end();
            return;
        }

        cam.update();

        batch.setProjectionMatrix(cam.combined);
		batch.begin();

		batch.draw(img, 0, 0);

        for(int i=0; i<entities.size(); i++) {
            Entity e = entities.get(i);
            if(e!=null) e.render(batch);
        }

        for(int i=0; i<particles.size(); i++) {
            if(particles.get(i)!=null) particles.get(i).render(batch);
        }

        for(int i=0; i<players.size(); i++) {
            Player p = players.get(i);
            p.render(batch);
        }

        for(int i=0; i<exclams.size(); i++) {
            exclams.get(i).render(batch);
        }

        for(int i=0; i<actionMessages.size(); i++) {
            actionMessages.get(i).render(batch, this);
        }

		batch.end();

        // tug of war bar
		renderBar();

		if(queueParticles!=-1) {
            switch(queueParticles) {
                case PLANT:
                    particles.add(new Action(queuePX, queuePY, "seed"));
                    break;
                case FIRE:
                    particles.add(new Action(queuePX, queuePY, "fire"));
                    break;
                case WATER:
                    particles.add(new Action(queuePX, queuePY, "water"));
                    break;
            }
            queueParticles = -1;
        }

        tmrMessages+=Gdx.graphics.getDeltaTime();
        int msgDelay = 3;
        if(tmrMessages>=msgDelay) {
            if(messageNum<4) messageNum++;
            tmrMessages = 0;
        }

        if(messageNum==0) addActionMessage("Use 'WASD' to Move", Color.WHITE);
        if(messageNum==1) {
            String role = "";
            switch(getMe().type) {
                case PLANT:
                    role = "plant seeds";
                    break;
                case FIRE:
                    role = "start fires";
                    break;
                case WATER:
                    role = "water plants/put out fires";
            }
            addActionMessage("Use 'Space' to " + role, Color.WHITE);
        }
        if(messageNum==2) addActionMessage("Use 'E' to call for attention", Color.WHITE);
        if(messageNum==3) addActionMessage("Win the tug of war", Color.WHITE);
	}

    public void renderBar() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        float width = SCREEN_W;
        float height = 12;
        float xp = 0;
        float yp = SCREEN_H-height;

        float req = getCount(PLANT)+getCount(FIRE);
        float perc = getCount(PLANT) / req * width;

        if(perc>width) perc = width;
        if(perc<0) perc = 0;

        float g = 0.6f + (getCount(PLANT) / req);
        if(g>1f) g = 1f;

        Color col = new Color(0.3f, g, 0.3f,0.5f);
        sr.setColor(col);
        sr.rect(xp, yp, perc, height);

        float r = 0.6f + (getCount(FIRE) / req);
        if(r>1f) r = 1f;
        col = new Color(r, 0.3f, 0.3f, 0.5f);
        sr.setColor(col);
        sr.rect(xp+perc, yp, width-perc, height);

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void addActionMessage(String message, Color colour) {
        // make sure its not like any others
        for(ActionMessage am : actionMessages) {
            if(message.equals(am.text)) return;
        }

        playSound("select");

        int size = 11;
        if(actionMessages.size()>=1) {
            actionMessages.add(new ActionMessage(message, size, colour));
            actionMessages.remove(0);
        } else {
            actionMessages.add(new ActionMessage(message, size, colour));
        }
    }

	@Override
	public void dispose () {
        batch.dispose();
        img.dispose();
	}

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode==Input.Keys.E) {
	        if(me!=-1 && getMe().canAtt) {
                exclams.add(new Exclam(getMe().x, getMe().y));
                Packets.Attention a = new Packets.Attention();
                a.x = getMe().x;
                a.y = getMe().y;
                n.client.sendUDP(a);
                playSound("attention");
                getMe().canAtt=false;
            }
        }

        if(keycode==Input.Keys.SPACE && me==-1) {
            n = new Network(this, ip, tcp, udp);
        }

        if(keycode==Input.Keys.F) {
            if(!Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(640, 480);
            }
        }

        if(keycode==Input.Keys.ESCAPE) {
            if(Gdx.graphics.isFullscreen()) Gdx.app.exit();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
