package net.sleepystudios.ld38.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.sleepystudios.ld38.LD38;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Entropy";
		config.width = LD38.SCREEN_W;
		config.height = LD38.SCREEN_H;
		config.resizable = false;

		new LwjglApplication(new LD38(), config);
	}
}
