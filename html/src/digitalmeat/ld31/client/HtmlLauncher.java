package digitalmeat.ld31.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import digitalmeat.ld31.Game;

public class HtmlLauncher extends GwtApplication {

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(1280, 700);
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return new Game();
	}
}