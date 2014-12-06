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
	private boolean dropping;

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
				Actions.color(Color.WHITE, Game.TILE_DROP_DURATION)
				, Actions.sizeTo(0f, 0f, Game.TILE_DROP_DURATION)
//				, Actions.scaleTo(0, 0, Game.TILE_PULSE_DURATION * 4)
			)
			, Actions.run(new Runnable() {
				
				@Override
				public void run() {
					drawing = false;
				}
			})
			, Actions.color(Color.WHITE)
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
