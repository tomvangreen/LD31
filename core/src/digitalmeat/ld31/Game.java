package digitalmeat.ld31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture tile;
	Texture player;
	OrthographicCamera cam;
	Viewport viewport;
	Field field;
	Stage stage;
	Array<KeyAndDelay> fades = new Array<KeyAndDelay>(true, 10);
	Array<String> levelKeys = new Array<String>(true, 10);
	LevelManager levels;

	@Override
	public void create() {
		cam = new OrthographicCamera(TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		viewport = new FitViewport(TILESCREEN_WIDTH, TILESCREEN_HEIGHT, cam);
		batch = new SpriteBatch();
		tile = new Texture("tile.png");
		player = new Texture("player.png");
		TemplateManager templates = new TemplateManager(TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		templates.load("title", "title.png");
		templates.load("entire", "title_entire.png");
		templates.load("game", "title_game.png");
		templates.load("on", "title_on.png");
		templates.load("one", "title_one.png");
		templates.load("screen", "title_screen.png");
		templates.load("level-01", "level-01.png");
		templates.load("level-02", "level-02.png");
		templates.load("level-03", "level-03.png");
		field = new Field(templates, tile, TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		stage = new Stage(viewport);
		stage.addActor(field.createGroup());

		fades.add(new KeyAndDelay("title", 0.5f));
		fades.add(new KeyAndDelay(null, 3f));
		fades.add(new KeyAndDelay("entire", 3f));
		fades.add(new KeyAndDelay("game", 2.5f));
		fades.add(new KeyAndDelay("on", 2.5f));
		fades.add(new KeyAndDelay("one", 2.5f));
		fades.add(new KeyAndDelay("screen", 2.5f));
		fades.add(new KeyAndDelay(null, 3f));
		fades.add(new KeyAndDelay(null, 2f));
		// fades.add(new KeyAndDelay("level-01", 3f));
		// fades.add(new KeyAndDelay("level-02", 4f));
		// fades.add(new KeyAndDelay("level-03", 4f));
		levels = new LevelManager(field, TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		levels.load("level-01", "level-01.png");
		levels.load("level-02", "level-02.png");
		levels.load("level-03", "level-03.png");
		levelKeys.add("level-01");
		levelKeys.add("level-02");
		levelKeys.add("level-03");
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	private float timer;

	int currentLevel = 0;
	boolean started = false;

	@Override
	public void render() {
		timer += Gdx.graphics.getDeltaTime();
		if (fades.size > 0) {
			KeyAndDelay template = fades.get(0);
			if (timer > template.delay) {
				if (template.key != null) {
					Gdx.app.log("Game", "LoadTemplate");
					field.loadTemplate(template.key);
				} else {
					Gdx.app.log("Game", "FadeOut");
					field.fadeOut();
				}
				timer = 0;
				fades.removeIndex(0);
			}

		} else {
			if (currentLevel < levelKeys.size) {
				if (!started) {
					Gdx.app.log("Game", "StartLevel(" + currentLevel + ")");
					field.loadLevel(levels.levels.get(levelKeys.get(currentLevel)));
					started = true;
				}
			}
		}
		stage.act();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();

		stage.draw();
		// batch.setProjectionMatrix(cam.combined);
		// batch.begin();
		// batch.end();

	}

	public final static int VIEWPORT_WIDTH = 1280;
	public final static int VIEWPORT_HEIGHT = 700;
	public final static int TILESCREEN_WIDTH = 32;
	public final static int TILESCREEN_HEIGHT = 20;

	public static class KeyAndDelay {
		String key;
		float delay;

		public KeyAndDelay(String key, float delay) {
			this.key = key;
			this.delay = delay;
		}
	}

}
