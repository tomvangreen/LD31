package digitalmeat.ld31;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class GameInputProcessor implements InputProcessor {

	private final Game game;

	public GameInputProcessor(Game game) {
		this.game = game;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ENTER) {
			boolean fullscreen = !Gdx.graphics.isFullscreen();
			int viewportWidth = Game.VIEWPORT_WIDTH;
			int viewportHeight = Game.VIEWPORT_HEIGHT;
			// if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
			viewportWidth = Gdx.graphics.getDesktopDisplayMode().width;
			viewportHeight = Gdx.graphics.getDesktopDisplayMode().height;
			// }
			Gdx.app.log("GameInputProcessor", "Changing fullscreen to: " + viewportWidth + "x" + viewportHeight + (fullscreen ? " fullscreen" : ""));
			Gdx.graphics.setDisplayMode(viewportWidth, viewportHeight, fullscreen);
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
