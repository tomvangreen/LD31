package eu32k.libgdx.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import eu32k.libgdx.common.Time;

public class AdvancedShader extends ShaderProgram {

	private boolean clear = true;

	public AdvancedShader(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	public void renderToScreeQuad(float xRes, float yRes, boolean flip) {
		renderToQuad(null, xRes, yRes, flip);
	}

	public void renderToScreeQuad(float xRes, float yRes) {
		renderToQuad(null, xRes, yRes, false);
	}

	public void renderToQuad(DynamicFrameBuffer frameBuffer, float xRes, float yRes, boolean flip) {
		if (hasUniform("time")) {
			setUniformf("time", Time.getTime());

		}
		if (hasUniform("resolution")) {
			setUniformf("resolution", xRes, yRes);
		}

		if (frameBuffer != null) {
			frameBuffer.begin();
			if (clear) {
				Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
		}
		if (flip) {
			Primitives.QUAD_FLIPPED.render(this, GL20.GL_TRIANGLE_FAN);
		} else {
			Primitives.QUAD_NORMAL.render(this, GL20.GL_TRIANGLE_FAN);
		}

		if (frameBuffer != null) {
			frameBuffer.end();
		}
		end();
	}

	public boolean isClear() {
		return clear;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}
}