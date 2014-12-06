package digitalmeat.ld31.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import digitalmeat.ld31.Game;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Game.VIEWPORT_WIDTH;
		config.height = Game.VIEWPORT_HEIGHT;
		config.foregroundFPS = 60;
		config.backgroundFPS = -1;
		new LwjglApplication(new Game(), config);
	}
}
