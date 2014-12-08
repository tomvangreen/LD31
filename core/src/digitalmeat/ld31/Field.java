package digitalmeat.ld31;

import ch.digitalmeat.grid.util.Table;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import digitalmeat.ld31.Level.TileConfig;
import digitalmeat.ld31.TemplateManager.Template;

public class Field {
	public final Table<Tile> table;
	public final Texture field;
	public final TemplateManager templates;

	public Field(TemplateManager templates, Texture field, int width, int height) {
		this.templates = templates;
		this.field = field;
		table = new Table<Tile>(width, height);
	}

	public Group createGroup() {
		Group group = new Group();
		for (int y = 0; y < table.height; y++) {
			for (int x = 0; x < table.width; x++) {
				float delay = calculateDelay(x, y);
				Tile tile = new Tile(field);
				tile.fieldPosition.set(x, y);
				tile.setSize(1, 1);
				tile.setPosition(x, y);
				tile.targetColor.set(Game.OFF_COLOR);
				table.set(x, y, tile);
				group.addActor(tile);
			}
		}
		return group;
	}

	public void fadeIn() {
		for (int y = 0; y < table.height; y++) {
			for (int x = 0; x < table.width; x++) {
				float delay = calculateDelay(x, y);
				Tile tile = table.get(x, y);
				tile.clearActions();
				//@formatter:off
				tile.addAction(
					Actions.sequence(
							Actions.color(Game.OFF_COLOR, delay)
							, tile.createPulse()
					)
				);
				//@formatter:on
			}
		}
	}

	public void fadeOut() {
		for (int y = 0; y < table.height; y++) {
			for (int x = 0; x < table.width; x++) {
				float delay = calculateDelay(x, y);
				Tile tile = table.get(x, y);
				tile.clearActions();
				//@formatter:off
				tile.addAction(
					Actions.sequence(
							Actions.delay(delay)
							, Actions.color(Game.OFF_COLOR, Game.TILE_FADE_DURATION)
					)
				);
				//@formatter:on
			}
		}
	}

	public void loadTemplate(String key) {
		Template template = templates.getTemplate(key);
		for (int y = 0; y < table.height; y++) {
			for (int x = 0; x < table.width; x++) {
				Tile tile = table.get(x, y);
				tile.targetColor.set(getTargetColor(template, x, y));
				tile.drawing = true;
				tile.dropped = false;
				tile.spriteRotation = 0f;
				tile.setSize(1, 1);
			}
		}
		fadeIn();
	}

	public void loadLevel(Level level) {
		if (level != null) {
			for (int y = 0; y < table.height; y++) {
				for (int x = 0; x < table.width; x++) {
					Tile tile = table.get(x, y);
					tile.targetColor.set(getTargetColor(level, x, y));
					TileConfig config = level.table.get(x, y);
					tile.type = config.type;
					tile.drawing = true;
					tile.dropped = false;
					tile.spriteRotation = 0f;
					tile.setSize(1, 1);
				}
			}
			fadeIn();
		} else {
			Gdx.app.error("Field", "Cannot load level. Level is null.");
		}
	}

	private Color getTargetColor(Template template, int x, int y) {
		if (template != null) {
			Color color = template.table.get(x, y);
			if (color != null) {
				return color;
			}
		}
		return Game.ON_COLOR;
	}

	private Color getTargetColor(Level template, int x, int y) {
		if (template != null) {
			TileConfig config = template.table.get(x, y);
			if (config != null && config.color != null) {
				return config.color;
			}
		}
		return Game.ON_COLOR;
	}

	public float calculateDelay(int x, int y) {
		return x * Game.X_DELAY + (table.height - y - 1) * Game.Y_DELAY;
	}

}
