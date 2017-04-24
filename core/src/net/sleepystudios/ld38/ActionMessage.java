package net.sleepystudios.ld38;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Created by Tudor on 24/04/2017.
 */
public class ActionMessage {
    public static final int DEFAULT_SIZE = 18;

    public String text;
    private int size;
    private Color colour;
    private float y = -1, tmrLife;
    private BitmapFont font;

    public ActionMessage(String text) {
        this(text, DEFAULT_SIZE, Color.WHITE);
    }

    public ActionMessage(String text, int size, Color colour) {
        this.text = text;
        this.size = size;
        this.colour = colour;
    }

    private void initFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Freeroad.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;
        parameter.borderWidth = 1;
        parameter.borderColor = new Color(0.3f, 0.3f, 0.3f, 0.5f);
        parameter.spaceX--;
        parameter.color = colour;

        font = generator.generateFont(parameter);
        generator.dispose();
    }

    private void checkFont() {
        if(font==null) initFont();
    }

    public void render(SpriteBatch batch, LD38 game) {
        checkFont();

        boolean winMsg = text.toLowerCase().contains("again");

        int index = LD38.actionMessages.indexOf(this);
        float tar = game.getMe().y + 40 + (index*20);
        if(winMsg) tar = Gdx.graphics.getHeight()/2 + (index*30);

        y+=(tar-y)*0.2f;
        if(!winMsg) tmrLife+=Gdx.graphics.getDeltaTime();

        if(tmrLife>=3) {
            if(font.getColor().a-0.1f>0) {
                font.getColor().a-=0.1f;
            } else {
                LD38.actionMessages.remove(this);
                return;
            }
        }

        GlyphLayout gl = new GlyphLayout(font, text);
        font.draw(batch, text, game.getMe().x+8-gl.width/2, y);
    }
}
