package net.sleepystudios.ld38.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.sleepystudios.ld38.LD38;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LD38 Land";
		config.width = 640;
		config.height = 480;
		config.resizable = false;

		new LwjglApplication(new LD38(), config);
	}
}
