package digitalmeat.ld31;

import ch.digitalmeat.grid.util.Table;
import ch.digitalmeat.util.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import digitalmeat.ld31.Tile.TileType;

public class Level {

	public final Table<TileConfig> table;
	public final Point start = new Point();
	public int foodTiles;
	public int keys;
	public final Array<Point> locked = new Array<Point>();

	public Level(int width, int height) {
		this.table = new Table<TileConfig>(width, height);
	}

	public static class TileConfig {
		public TileType type;
		public final int x;
		public final int y;
		public Color color;

		public TileConfig(int x, int y, TileType type, Color color) {
			this.x = x;
			this.y = y;
			this.type = type;
		}
	}
}
