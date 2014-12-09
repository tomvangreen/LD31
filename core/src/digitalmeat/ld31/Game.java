package digitalmeat.ld31;

import ch.digitalmeat.util.Point;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
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
	boolean sequences = true;
	boolean spritesDisabled;
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
	public Music theme;
	SoundManager sounds = new SoundManager();

	private Stats stats = new Stats();

	@Override
	public void create() {
		sounds.create();
		theme = Gdx.audio.newMusic(Gdx.files.internal("ld31-theme-02.ogg"));
		theme.setLooping(true);
		theme.play();
		cam = new OrthographicCamera(TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		viewport = new FitViewport(TILESCREEN_WIDTH, TILESCREEN_HEIGHT, cam);
		batch = new SpriteBatch();
		tile = new Texture("tile.png");
		player = new Texture("player.png");
		TemplateManager templates = new TemplateManager(TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		templates.load("title", "title.png");
		templates.load("pr1", "pixelrun.png");
		templates.load("pr2", "pixelrun2.png");
		templates.load("pr3", "pixelrun3.png");
		templates.load("pr4", "pixelrun4.png");
		templates.load("entire", "title_entire.png");
		templates.load("game", "title_game.png");
		templates.load("entire_game", "title_entire_game.png");
		templates.load("on_one_screen", "title_on_one_screen.png");
		templates.load("on", "title_on.png");
		templates.load("one", "title_one.png");
		templates.load("screen", "title_screen.png");
		templates.load("c1", "credits-grats2.png");
		templates.load("c2", "credits-grats.png");
		templates.load("c3", "credits-madeby.png");
		templates.load("c4", "special-thanks.png");
		templates.load("c5", "to-cthulhu.png");
		templates.load("c6", "for-map.png");

		field = new Field(templates, tile, TILESCREEN_WIDTH, TILESCREEN_HEIGHT);
		stage = new Stage(viewport);
		stage.addActor(field.createGroup());
		playerActor = new PlayerActor(player);
		stage.addActor(playerActor);
		plexer.addProcessor(stage);
		plexer.addProcessor(new GameInputProcessor(this));
		Gdx.input.setInputProcessor(plexer);

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
			addLevel("01-intro-level");
			addLevel("02-reset-button");
			addLevel("03-food");
			addLevel("04-get-all-food");
			addLevel("05-introducing-keys-01");
			addLevel("05-introducing-keys-02");
			addLevel("05-introducing-keys-03");
			addLevel("05-introducing-keys-04");
			addLevel("10-medium-01-bigger-lock");
			addLevel("10-medium-02-cthulhu");
			addLevel("20-hard-01");
			addLevel("20-hard-02");
			addLevel("20-hard-03");
			addLevel("20-hard-04");
			addLevel("20-hard-05");
			addLevel("99-bonuz-02");
			addLevel("99-bonuz");
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

	public void addLevel(String key) {
		levelKeys.add("levels/" + key + ".png");
	}

	public void createIntroSequence() {
		if (!sequences) {
			return;
		}
		fades.add(new KeyAndDelay(null, 1f));
		fades.add(new KeyAndDelay("title", 0.5f, true));
		fades.add(new KeyAndDelay(null, 3f));
		fades.add(new KeyAndDelay("entire_game", 3f));
		fades.add(new KeyAndDelay("on_one_screen", 2.5f, true));
		fades.add(new KeyAndDelay(null, 3f));
		fades.add(new KeyAndDelay("pr4", 3f, true));
		fades.add(new KeyAndDelay("pr3", 1.8f, true));
		fades.add(new KeyAndDelay("pr2", 3f));
		fades.add(new KeyAndDelay("pr1", 1.8f));
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
		if (!sequences) {
			return;
		}
		fades.add(new KeyAndDelay(null, 1.5f));
		fades.add(new KeyAndDelay("c1", 3f));
		fades.add(new KeyAndDelay("c2", 3f));
		fades.add(new KeyAndDelay("pr4", 3f));
		fades.add(new KeyAndDelay("c3", 2f));
		fades.add(new KeyAndDelay("c4", 4f));
		fades.add(new KeyAndDelay("c5", 3f));
		fades.add(new KeyAndDelay("c6", 3f));
		fades.add(new KeyAndDelay(null, 3f));
		fades.add(new KeyAndDelay(null, 1f));
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
	private int foundKeys;

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
				field.drawIcons = false;
				startTimer -= deltaTime;
				if (startTimer < 0) {
					playerActor.alive = true;
					field.drawIcons = !spritesDisabled;
					sounds.playStartSound();
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
						if (canMoveToTile(tile)) {
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
						if (canMoveToTile(tile)) {
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
						sounds.playFoodSound();
					} else if (leaving.type == TileType.Key) {
						sounds.playKeySound();
						foundKeys++;
						field.unlocked = currentLevel.keys == foundKeys;
						if (foundKeys == currentLevel.keys) {
							for (Point lockPosition : currentLevel.locked) {
								Tile tile = field.table.get(lockPosition.x, lockPosition.y);
								if (tile != null) {
									tile.clearActions();
									tile.targetColor.set(ON_COLOR);
									tile.addAction(tile.createPulse());
								}
							}
						}
					} else {
						sounds.playEmptySound();
					}

				}

			}
			Tile tile = field.table.get(tempPoint.x, tempPoint.y);
			if (tile != null && tile.type == TileType.Goal && foodFound == currentLevel.foodTiles) {
				field.drawIcons = false;
				currentLevel = null;
				currentLevelIndex++;
				sounds.playWinSound();
				// playerActor.addAction(Actions.color(OFF_COLOR,
				// TILE_FADE_DURATION));
				started = false;
			}
		}
	}

	public boolean canMoveToTile(Tile tile) {
		return tile != null && tile.type.walkable && !tile.dropped && (tile.type != TileType.Door || foundKeys == currentLevel.keys);
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
			sounds.playResetSound();
			currentLevel = null;
			started = false;
		}
	}

	public void updateFades() {
		KeyAndDelay template = fades.get(0);
		if (timer > template.delay) {
			if (template.key != null) {
				Gdx.app.log("Game", "LoadTemplate");
				field.loadTemplate(template.key, template.fadeIn);
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
		foundKeys = 0;
		playerActor.setSize(1, 1);
		//@formatter:off
		playerActor.addAction(
			Actions.sequence(
				Actions.alpha(0f, FIELD_DELAY / 2)
				, Actions.moveTo(currentLevel.start.x, currentLevel.start.y)
//				, Actions.color(Color.BLUE, FIELD_DELAY / 2)
				, playerActor.createPulse()
			)
		);
		//@formatter:on
		startTimer = FIELD_DELAY;
		startTimerOn = true;
		field.unlocked = currentLevel.keys == foundKeys;
		started = true;

	}

	public final static int VIEWPORT_WIDTH = 1280;
	public final static int VIEWPORT_HEIGHT = 720;
	public final static int TILESCREEN_WIDTH = 32;
	public final static int TILESCREEN_HEIGHT = 20;
	public static final float X_DELAY = 0.03f;
	public static final float Y_DELAY = 0.05f;
	public static final float X_DELAY_TOTAL = TILESCREEN_WIDTH * X_DELAY;
	public static final float Y_DELAY_TOTAL = TILESCREEN_HEIGHT * Y_DELAY;
	public static final float TILE_FADE_DURATION = 1f;
	public static final float TILE_PULSE_DURATION = 1.5f;
	public static final float TILE_DROP_DURATION = 0.5f;
	public static final float TILE_PULSE_OFFSET = 0.05f;
	public static final float FIELD_DELAY = Math.max(X_DELAY_TOTAL, Y_DELAY_TOTAL) + TILE_FADE_DURATION + TILE_PULSE_DURATION;
	public static final float PLAYER_MOVE_SPEED = 5;
	public static final float TILE_DROP_ROTATION_SPEED = 180;

	// public final static Color OFF_COLOR = new Color(0, 0, 0, 0);
	// public final static Color ON_COLOR = new Color(Color.WHITE);

	public final static Color ON_COLOR = new Color(0, 0, 0, 1);
	public final static Color OFF_COLOR = new Color(1, 1, 1, 0);

	public static class KeyAndDelay {
		public boolean fadeIn;
		String key;
		float delay;

		public KeyAndDelay(String key, float delay) {
			this(key, delay, false);
		}

		public KeyAndDelay(String key, float delay, boolean fadeIn) {
			this.key = key;
			this.delay = delay;
		}
	}

}
