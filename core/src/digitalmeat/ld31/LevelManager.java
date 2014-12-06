package digitalmeat.ld31;

import java.util.HashMap;
import java.util.Map;

import ch.digitalmeat.util.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import digitalmeat.ld31.Level.TileConfig;
import digitalmeat.ld31.Tile.TileType;

public class LevelManager {
	public final Field field;
	public final int width;
	public final int height;

	public final Map<String, Level> levels = new HashMap<String, Level>();

	public LevelManager(Field field, int width, int height) {
		this.field = field;
		this.width = width;
		this.height = height;
	}

	public void load(String key, String file) {
		Pixmap pixmap = new Pixmap(Gdx.files.internal(file));
		int endX = Math.min(width, pixmap.getWidth());
		int endY = Math.min(height, pixmap.getHeight());
		Level level = new Level(width, height);
		level.foodTiles = 0;
		level.start.set(0, 0);
		level.keys = 0;
		level.locked.clear();
		for (int y = 0; y < endY; y++) {
			for (int x = 0; x < endX; x++) {
				TileType type = TileType.Empty;
				Color color = new Color(pixmap.getPixel(x, y));
				if (color.equals(Color.BLUE)) {
					type = TileType.Start;
					level.start.set(x, y);
				} else if (color.equals(Color.GREEN)) {
					type = TileType.Food;
					level.foodTiles++;
				} else if (color.equals(Color.YELLOW)) {
					type = TileType.Key;
					level.keys++;
				} else if (color.equals(Color.RED)) {
					type = TileType.Door;
					level.locked.add(new Point(x, y));
				} else if (color.a == 0f) {
					type = TileType.Empty;
				} else {
					type = TileType.Walkable;
				}
				TileConfig template = new TileConfig(x, y, type, color);
				level.table.set(x, y, template);

			}
		}
		levels.put(key, level);
	}

	public void loadLevel(String key) {
		Level level = levels.get(key);
		field.loadLevel(level);
	}
}
