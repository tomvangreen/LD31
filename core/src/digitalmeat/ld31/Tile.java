package digitalmeat.ld31;

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
	}

	public Action createPulse() {
		tempColor.set(targetColor);
		tempColor.r = Math.min(1f, tempColor.r + Game.TILE_PULSE_OFFSET);
		tempColor.g = Math.min(1f, tempColor.g + 0.13f);
		tempColor.b = Math.min(1f, tempColor.b + 0.13f);
		tempColor.a = Math.min(1f, tempColor.a + 0.13f);
		//@formatter:off
		return Actions.forever(
			Actions.sequence(
				Actions.color(tempColor, Game.TILE_PULSE_DURATION)
				, Actions.color(targetColor, 0.5f)
			)
		);
		//@formatter:on
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.setPosition(getX(), getY());
		sprite.setRotation(getRotation());
		sprite.setSize(getWidth(), getHeight());
		sprite.setColor(getColor());
		sprite.draw(batch, parentAlpha);
	}

	public static enum TileType {
		Empty(false), Start(true), Food(true), Key(true), Door(true, true);
		public final boolean walkable;
		public final boolean locked;

		TileType(boolean walkable) {
			this(walkable, false);
		}

		TileType(boolean walkable, boolean locked) {
			this.walkable = true;
			this.locked = locked;
		}
	}
}
