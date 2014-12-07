package digitalmeat.ld31;

import java.util.HashMap;
import java.util.Map;

import ch.digitalmeat.util.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Tile extends Actor {
	public final Sprite sprite;
	public final Point fieldPosition = new Point();

	public final Color targetColor = new Color();
	public final Color tempColor = new Color();
	public TileType type = TileType.Empty;
	public boolean dropped;

	public Tile(Texture texture) {
		this.sprite = new Sprite(texture);
		setColor(Game.OFF_COLOR);
	}

	public Action createPulse() {
		tempColor.set(targetColor);
		float offset = Game.TILE_PULSE_OFFSET;
		if (Game.ON_COLOR.r > Game.OFF_COLOR.r) {
			offset *= -1;
		}
		tempColor.r = Math.min(1f, tempColor.r + offset);
		tempColor.g = Math.min(1f, tempColor.g + offset);
		tempColor.b = Math.min(1f, tempColor.b + offset);
		tempColor.a = Math.min(1f, tempColor.a + offset);
		//@formatter:off
		return Actions.forever(
			Actions.sequence(
				Actions.color(tempColor, Game.TILE_PULSE_DURATION / 2)
				, Actions.color(targetColor, Game.TILE_PULSE_DURATION / 2)
			)
		);
		//@formatter:on
	}

	public float spriteRotation;

	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		if (dropped) {
			spriteRotation += Game.TILE_DROP_ROTATION_SPEED * deltaTime;
			while (spriteRotation < 0) {
				spriteRotation += spriteRotation;
			}
			while (spriteRotation > 360) {
				spriteRotation = 360;
			}
		}
	}

	boolean drawing = true;

	public void drop() {
		spriteRotation = 0;
		dropped = true;
		clearActions();
		addAction(createDrop());
	}

	public Action createDrop() {
		//@formatter:off
		return Actions.sequence(
			Actions.parallel(
				Actions.color(Game.OFF_COLOR, Game.TILE_DROP_DURATION)
				, Actions.sizeTo(0f, 0f, Game.TILE_DROP_DURATION)
//				, Actions.scaleTo(0, 0, Game.TILE_PULSE_DURATION * 4)
			)
			, Actions.run(new Runnable() {
				
				@Override
				public void run() {
					drawing = false;
				}
			})
			, Actions.color(Game.OFF_COLOR)
			, Actions.sizeTo(1, 1)
		);
		//@formatter:on
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (drawing) {
			float width = getWidth();
			float height = getHeight();
			float x = getX() + (1 - width) / 2;
			float y = getY() + (1 - height) / 2;
			sprite.setPosition(x, y);
			sprite.setRotation(spriteRotation);
			sprite.setSize(width, height);
			sprite.setColor(getColor());
			sprite.setOriginCenter();
			sprite.setScale(getScaleX(), getScaleY());
			sprite.draw(batch, parentAlpha);
		}
	}

	public static enum TileType {
		Empty(false), Walkable(true), Start(true, Color.BLUE), Food(true, Color.GREEN), Key(true, Color.YELLOW), Door(true, Color.RED, true), Goal(true, Color.CYAN);
		public final boolean walkable;
		public final boolean locked;
		public final Color color;

		TileType(boolean walkable) {
			this(walkable, null, false);
		}

		TileType(boolean walkable, Color color) {
			this(walkable, color, false);
		}

		TileType(boolean walkable, Color color, boolean locked) {
			this.walkable = walkable;
			this.locked = locked;
			this.color = color;
		}

		private static Map<Color, TileType> typeMap;

		public static TileType getTypeByColor(Color color) {
			if (typeMap == null) {
				createTypeMap();
			}
			TileType type = typeMap.get(color);
			if (type == null) {
				type = color.a == 0 ? TileType.Empty : TileType.Walkable;
			}
			return type;
		}

		public static void createTypeMap() {
			typeMap = new HashMap<Color, TileType>();
			for (TileType type : TileType.values()) {
				if (type.color != null) {
					typeMap.put(type.color, type);
				}
			}
		}
	}
}
