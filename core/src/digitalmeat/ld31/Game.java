package digitalmeat.ld31;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
	Vector2 playerPosition = new Vector2();
	InputMultiplexer plexer = new InputMultiplexer();
	private PlayerActor playerActor;

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
		playerActor = new PlayerActor(player);
		stage.addActor(playerActor);
		plexer.addProcessor(stage);
		plexer.addProcessor(new GameInputProcessor(this));
		createIntroSequence();
		levels = new LevelManager(field, TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		levels.load("level-01", "level-01.png");
		levels.load("level-02", "level-02.png");
		levels.load("level-03", "level-03.png");
		levelKeys.add("level-01");
		levelKeys.add("level-02");
		levelKeys.add("level-03");
	}

	public void createIntroSequence() {
		fades.add(new KeyAndDelay("title", 0.5f));
		// fades.add(new KeyAndDelay(null, 3f));
		// fades.add(new KeyAndDelay("entire", 3f));
		// fades.add(new KeyAndDelay("game", 2.5f));
		// fades.add(new KeyAndDelay("on", 2.5f));
		// fades.add(new KeyAndDelay("one", 2.5f));
		// fades.add(new KeyAndDelay("screen", 2.5f));
		fades.add(new KeyAndDelay(null, 3f));
		Gdx.app.log("Game", "Delay: " + FIELD_DELAY);
		fades.add(new KeyAndDelay(null, FIELD_DELAY));
		// fades.add(new KeyAndDelay("level-01", 3f));
		// fades.add(new KeyAndDelay("level-02", 4f));
		// fades.add(new KeyAndDelay("level-03", 4f));
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	private float timer;

	int currentLevelIndex = 0;
	Level currentLevel;
	boolean started = false;
	boolean startTimerOn = false;

	@Override
	public void render() {
		act();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();

		stage.draw();
		// batch.setProjectionMatrix(cam.combined);
		// batch.begin();
		// batch.end();

	}

	private final Vector2 move = new Vector2();

	public void act() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (deltaTime > 0.5f) {
			deltaTime = 0.5f;
		}
		timer += deltaTime;
		if (fades.size > 0) {
			updateFades();

		} else {
			if (currentLevel == null || !started) {
				startLevel();
			} else if (startTimerOn) {
				startTimer -= deltaTime;
				if (startTimer < 0) {
					playerActor.alive = true;
					startTimerOn = false;
				}
			}
			move.set(0, 0);
			if (started && !startTimerOn) {
				if (playerActor.alive) {
					updateInput();
					move.nor().scl(deltaTime * PLAYER_MOVE_SPEED);
					playerActor.moveBy(move.x, move.y);
				}
			}
		}
		stage.act();
	}

	public void updateInput() {
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			move.x -= 1;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
			move.x += 1;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.D)) {
			move.y -= 1;
		}
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			move.y += 1;
		}
	}

	public void updateFades() {
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
	}

	float startTimer = 0;

	public void startLevel() {
		Gdx.app.log("Game", "StartLevel(" + currentLevelIndex + ")");
		currentLevel = levels.levels.get(levelKeys.get(currentLevelIndex));
		field.loadLevel(currentLevel);
		playerActor.setSize(1, 1);
		playerActor.setPosition(currentLevel.start.x, currentLevel.start.y);
		playerActor.addAction(Actions.color(Color.BLUE, 3f));
		startTimer = FIELD_DELAY;
		startTimerOn = true;
		started = true;
	}

	public final static int VIEWPORT_WIDTH = 1280;
	public final static int VIEWPORT_HEIGHT = 700;
	public final static int TILESCREEN_WIDTH = 32;
	public final static int TILESCREEN_HEIGHT = 20;
	public static final float X_DELAY = 0.03f;
	public static final float Y_DELAY = 0.05f;
	public static final float X_DELAY_TOTAL = TILESCREEN_WIDTH * X_DELAY;
	public static final float Y_DELAY_TOTAL = TILESCREEN_WIDTH * X_DELAY;
	public static final float TILE_FADE_DURATION = 1f;
	public static final float TILE_PULSE_DURATION = 0.5f;
	public static final float TILE_PULSE_OFFSET = 0.13f;
	public static final float FIELD_DELAY = Math.max(X_DELAY_TOTAL + TILE_FADE_DURATION, Y_DELAY_TOTAL + TILE_FADE_DURATION);
	private static final float PLAYER_MOVE_SPEED = 10;

	public static class KeyAndDelay {
		String key;
		float delay;

		public KeyAndDelay(String key, float delay) {
			this.key = key;
			this.delay = delay;
		}
	}

}
