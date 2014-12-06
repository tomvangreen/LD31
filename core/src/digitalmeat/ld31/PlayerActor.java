package digitalmeat.ld31;

import ch.digitalmeat.util.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlayerActor extends Actor {
	public final Sprite sprite;
	public boolean alive;
	public final Point fieldPosition = new Point();

	public PlayerActor(Texture texture) {
		this.sprite = new Sprite(texture);
		setColor(Color.WHITE);
		getColor().a = 0;
	}

	@Override
	public void positionChanged() {

	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		sprite.setPosition(getX() + getWidth() / 4, getY() + getHeight() / 4);
		sprite.setRotation(getRotation());
		sprite.setSize(getWidth() / 2, getHeight() / 2);
		sprite.setColor(getColor());
		sprite.draw(batch, parentAlpha);
	}
}
