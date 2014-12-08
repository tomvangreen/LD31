package digitalmeat.ld31;

import ch.digitalmeat.util.Point;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
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

import digitalmeat.ld31.Tile.TileType;
import eu32k.libgdx.rendering.AdvancedShader;
import eu32k.libgdx.rendering.DynamicFrameBuffer;

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
	private DynamicFrameBuffer mainBuffer;
	private DynamicFrameBuffer secondaryBuffer;
	private AdvancedShader mixerShader;
	private DynamicFrameBuffer blurBuffer1;
	private AdvancedShader verticalBlur;
	private DynamicFrameBuffer blurBuffer2;
	private AdvancedShader horizontalBlur;

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
		templates.load("entire_game", "title_entire_game.png");
		templates.load("on_one_screen", "title_on_one_screen.png");
		templates.load("on", "title_on.png");
		templates.load("one", "title_one.png");
		templates.load("screen", "title_screen.png");
		templates.load("c1", "credits-grats.png");
		templates.load("c2", "credits-madeby.png");

		field = new Field(templates, tile, TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		stage = new Stage(viewport);
		stage.addActor(field.createGroup());
		playerActor = new PlayerActor(player);
		stage.addActor(playerActor);
		plexer.addProcessor(stage);
		plexer.addProcessor(new GameInputProcessor(this));

		createIntroSequence();

		levels = new LevelManager(field, TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		if (externalLevels != null) {
			FileHandle folder = Gdx.files.local(externalLevels);
			if (folder.exists() && folder.isDirectory()) {
				for (FileHandle file : folder.list()) {
					if ("png".equalsIgnoreCase(file.extension())) {
						levelKeys.add(file.path());
					}
				}
			}

		} else {
			levelKeys.add("level-intro.png");
			levelKeys.add("level-02.png");
			levelKeys.add("level-food.png");
			levelKeys.add("level-03.png");
		}
		for (String key : levelKeys) {
			levels.load(key, key, externalLevels != null);
		}

		float scaleDown = 1f;
		mainBuffer = new DynamicFrameBuffer();
		secondaryBuffer = new DynamicFrameBuffer(scaleDown);
		mixerShader = new AdvancedShader(Gdx.files.internal("shaders/simple.vsh").readString(), Gdx.files.internal("shaders/mixer.fsh").readString());

		blurBuffer1 = new DynamicFrameBuffer(scaleDown);
		verticalBlur = new AdvancedShader(Gdx.files.internal("shaders/simple.vsh").readString(), Gdx.files.internal("shaders/blur_v.fsh").readString());

		blurBuffer2 = new DynamicFrameBuffer(scaleDown);
		horizontalBlur = new AdvancedShader(Gdx.files.internal("shaders/simple.vsh").readString(), Gdx.files.internal("shaders/blur_h.fsh").readString());

	}

	public void createIntroSequence() {
		fades.add(new KeyAndDelay("title", 0.5f));
		fades.add(new KeyAndDelay(null, 3f));
		fades.add(new KeyAndDelay("entire_game", 3f));
		fades.add(new KeyAndDelay("on_one_screen", 2.5f));
		fades.add(new KeyAndDelay(null, 3f));
		Gdx.app.log("Game", "Delay: " + FIELD_DELAY);
		fades.add(new KeyAndDelay(null, FIELD_DELAY));

		// fades.add(new KeyAndDelay("game", 2.5f));
		// fades.add(new KeyAndDelay("one", 2.5f));
		// fades.add(new KeyAndDelay("screen", 2.5f));
		// fades.add(new KeyAndDelay("level-01", 3f));
		// fades.add(new KeyAndDelay("level-02", 4f));
		// fades.add(new KeyAndDelay("level-03", 4f));
	}

	public void createOutroSequence() {
		fades.add(new KeyAndDelay(null, 3f));
		fades.add(new KeyAndDelay("c1", 3f));
		fades.add(new KeyAndDelay("c2", 3f));
		fades.add(new KeyAndDelay(null, 3f));
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
	private boolean useShader = false;

	@Override
	public void render() {
		act();
		if (useShader) {
			renderToScreen();
		} else {
			renderStage();
		}
	}

	public void renderToScreen() {
		mainBuffer.begin();
		renderStage();
		mainBuffer.end();

		secondaryBuffer.begin();
		renderStage();
		secondaryBuffer.end();

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		secondaryBuffer.bindTexture();
		verticalBlur.begin();
		verticalBlur.setUniformi("uTexture", 0);
		verticalBlur.renderToQuad(blurBuffer1, TILESCREEN_WIDTH, TILESCREEN_HEIGHT, true);

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		blurBuffer1.bindTexture();
		horizontalBlur.begin();
		horizontalBlur.setUniformi("uTexture", 0);
		horizontalBlur.renderToQuad(blurBuffer2, TILESCREEN_WIDTH, TILESCREEN_HEIGHT, true);

		// --

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
		mainBuffer.bindTexture();

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		blurBuffer2.bindTexture();

		mixerShader.begin();
		mixerShader.setUniformi("uTexture1", 1);
		mixerShader.setUniformi("uTexture2", 0);

		mixerShader.setUniformf("uFactor1", 1.0f);
		mixerShader.setUniformf("uFactor2", 1.6f);

		mixerShader.renderToScreeQuad(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		mixerShader.end();

		// batch.setProjectionMatrix(cam.combined);
		// batch.begin();
		// batch.end();
	}

	public void renderStage() {
		Gdx.gl.glClearColor(OFF_COLOR.r, OFF_COLOR.g, OFF_COLOR.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();
		stage.draw();
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
				if (currentLevelIndex >= levels.levels.size()) {
					currentLevelIndex = 0;
					createOutroSequence();
					createIntroSequence();

				} else {
					startLevel();
				}
			} else if (startTimerOn) {
				startTimer -= deltaTime;
				if (startTimer < 0) {
					playerActor.alive = true;
					startTimerOn = false;
				}
			}
			move.set(0, 0);
			if (started && !startTimerOn && !playerActor.transporting) {
				if (playerActor.alive) {
					updateInput();
					if (move.x != 0) {
						tempPoint.set(playerActor.fieldPosition);
						if (move.x > 0) {
							tempPoint.x++;
						} else {
							tempPoint.x--;
						}
						Tile tile = field.table.get(tempPoint.x, tempPoint.y);
						if (tile != null && tile.type.walkable && !tile.dropped) {
							playerActor.targetPoint.set(tempPoint);
							playerActor.transporting = true;
						}
					} else if (move.y != 0) {
						tempPoint.set(playerActor.fieldPosition);
						if (move.y > 0) {
							tempPoint.y++;
						} else {
							tempPoint.y--;
						}
						Tile tile = field.table.get(tempPoint.x, tempPoint.y);
						if (tile != null && tile.type.walkable && !tile.dropped) {
							playerActor.targetPoint.set(tempPoint);
							playerActor.transporting = true;
						}
					}
					// move.nor().scl(deltaTime * PLAYER_MOVE_SPEED);
					// playerActor.moveBy(move.x, move.y);
				}
			}
		}
		stage.act();
		if (started && !startTimerOn) {
			tempPoint.set((int) (playerActor.getX() + 0.5f), (int) (playerActor.getY() + 0.5f));
			if (!tempPoint.equals(playerActor.fieldPosition)) {
				Tile leaving = field.table.get(playerActor.fieldPosition.x, playerActor.fieldPosition.y);
				if (leaving != null) {
					if (!leaving.dropped && leaving.type != TileType.Goal) {
						leaving.drop();

					}
					if (leaving.type == TileType.Food) {
						foodFound++;
					}
				}

			}
			Tile tile = field.table.get(tempPoint.x, tempPoint.y);
			if (tile != null && tile.type == TileType.Goal && foodFound == currentLevel.foodTiles) {
				currentLevel = null;
				currentLevelIndex++;
				playerActor.addAction(Actions.color(OFF_COLOR, TILE_FADE_DURATION));
				started = false;
			}
		}
	}

	final Point tempPoint = new Point();

	public void updateInput() {
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			move.x -= 1;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
			move.x += 1;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
			move.y -= 1;
		}
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			move.y += 1;
		}
		if (Gdx.input.isKeyPressed(Keys.R)) {

			move.set(0, 0);
			currentLevel = null;
			started = false;
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
	int foodFound = 0;
	public String externalLevels;

	public void startLevel() {
		Gdx.app.log("Game", "StartLevel(" + currentLevelIndex + ")");
		currentLevel = levels.levels.get(levelKeys.get(currentLevelIndex));
		field.loadLevel(currentLevel);
		foodFound = 0;
		playerActor.setSize(1, 1);
		//@formatter:off
		playerActor.addAction(
			Actions.sequence(
				Actions.alpha(0f, FIELD_DELAY / 2)
				, Actions.moveTo(currentLevel.start.x, currentLevel.start.y)
				, Actions.color(Color.BLUE, FIELD_DELAY / 2)
			)
		);
		//@formatter:on
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
	public static final float Y_DELAY_TOTAL = TILESCREEN_HEIGHT * Y_DELAY;
	public static final float TILE_FADE_DURATION = 1f;
	public static final float TILE_PULSE_DURATION = 0.5f;
	public static final float TILE_DROP_DURATION = 0.5f;
	public static final float TILE_PULSE_OFFSET = 0.05f;
	public static final float FIELD_DELAY = Math.max(X_DELAY_TOTAL, Y_DELAY_TOTAL) + TILE_FADE_DURATION + TILE_PULSE_DURATION;
	public static final float PLAYER_MOVE_SPEED = 5;
	public static final float TILE_DROP_ROTATION_SPEED = 180;

	// public final static Color OFF_COLOR = new Color(0, 0, 0, 0);
	// public final static Color ON_COLOR = new Color(Color.WHITE);

	public final static Color ON_COLOR = new Color(0, 0, 0, 0);
	public final static Color OFF_COLOR = new Color(1, 1, 1, 0);

	public static class KeyAndDelay {
		String key;
		float delay;

		public KeyAndDelay(String key, float delay) {
			this.key = key;
			this.delay = delay;
		}
	}

}
