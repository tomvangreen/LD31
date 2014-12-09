package digitalmeat.ld31;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class SoundManager {
	private int emptyIndex;
	private final Array<Sound> emptySounds = new Array<Sound>(true, 6);
	private Sound food;
	private Sound key;
	private Sound reset;
	private Sound win;
	private Sound start;

	public void create() {
		int sounds = 6;
		for (int index = 1; index <= sounds; index++) {
			emptySounds.add(Gdx.audio.newSound(Gdx.files.internal("sfx/e" + index + ".wav")));
		}
		food = Gdx.audio.newSound(Gdx.files.internal("sfx/food.wav"));
		key = Gdx.audio.newSound(Gdx.files.internal("sfx/key.wav"));
		reset = Gdx.audio.newSound(Gdx.files.internal("sfx/reset.wav"));
		win = Gdx.audio.newSound(Gdx.files.internal("sfx/win.wav"));
		start = Gdx.audio.newSound(Gdx.files.internal("sfx/start.wav"));
	}

	public void playEmptySound() {
		Sound sound = emptySounds.get(emptyIndex);
		playSound(sound);
		emptyIndex++;
		emptyIndex = emptyIndex % emptySounds.size;
	}

	public void playWinSound() {
		playSound(win);
	}

	public void playStartSound() {
		playSound(start);
	}

	public void playResetSound() {
		playSound(reset);
	}

	public void playFoodSound() {
		playSound(food);
	}

	public void playKeySound() {
		playSound(key);
	}

	public void playSound(Sound sound) {
		sound.play(0.3f, 2f, 0);
	}
}
