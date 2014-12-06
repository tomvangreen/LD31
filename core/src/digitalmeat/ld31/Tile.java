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

	public Tile(Texture texture) {
		this.sprite = new Sprite(texture);
	}

	public Action createPulse() {
		tempColor.set(targetColor);
		tempColor.r = Math.min(1f, tempColor.r + 0.13f);
		tempColor.g = Math.min(1f, tempColor.g + 0.13f);
		tempColor.b = Math.min(1f, tempColor.b + 0.13f);
		tempColor.a = Math.min(1f, tempColor.a + 0.13f);
		//@formatter:off
		return Actions.forever(
			Actions.sequence(
				Actions.color(tempColor, 0.5f)
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
}
