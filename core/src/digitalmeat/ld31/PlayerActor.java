package digitalmeat.ld31;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlayerActor extends Actor {
	public final Sprite sprite;
	public boolean alive;

	public PlayerActor(Texture texture) {
		this.sprite = new Sprite(texture);
		setColor(Color.WHITE);
		getColor().a = 0;
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
