package digitalmeat.ld31;

import ch.digitalmeat.util.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class PlayerActor extends Actor {
	public final Sprite sprite;
	public boolean alive;
	public final Point fieldPosition = new Point();
	public final Point targetPoint = new Point();
	public boolean transporting;

	private final Vector2 v = new Vector2();

	public PlayerActor(Texture texture) {
		this.sprite = new Sprite(texture);
		setColor(Color.WHITE);
		getColor().a = 0;
	}

	@Override
	public void positionChanged() {

	}

	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		fieldPosition.set((int) (getX() + 0.5f), (int) (getY() + 0.5f));
		if (transporting) {
			v.set(targetPoint.x, targetPoint.y).sub(getX(), getY()).nor();
			v.scl(deltaTime * Game.PLAYER_MOVE_SPEED);
			setPosition(getX() + v.x, getY() + v.y);
			v.set(getX(), getY()).sub(targetPoint.x, targetPoint.y);
			if (v.len() < 0.05f) {
				transporting = false;
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.setPosition(getX() + getWidth() / 4, getY() + getHeight() / 4);
		sprite.setRotation(getRotation());
		sprite.setSize(getWidth() / 2, getHeight() / 2);
		sprite.setColor(getColor());
		sprite.draw(batch, parentAlpha);
	}

	private final Color tempColor = new Color();

	public Action createPulse() {
		float stepDuration = Game.TILE_PULSE_DURATION / 2;
		float offset = Game.TILE_PULSE_OFFSET;
		//@formatter:off
		return Actions.forever(
			Actions.sequence(
				Actions.color(Color.BLUE, stepDuration)
				, Actions.color(Color.PURPLE, stepDuration)
				, Actions.color(Color.RED, stepDuration)
				, Actions.color(Color.YELLOW, stepDuration)
				, Actions.color(Color.GREEN, stepDuration)
				, Actions.color(Color.CYAN, stepDuration)
			)
		);
		//@formatter:on
	}

}
