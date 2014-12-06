package eu32k.libgdx.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class DynamicFrameBuffer {

	private static int width = 16;
	private static int height = 16;
	private static List<DynamicFrameBuffer> buffers = new ArrayList<DynamicFrameBuffer>();

	public static void resetAllBuffers(int newWidth, int newHeight) {
		DynamicFrameBuffer.width = newWidth;
		DynamicFrameBuffer.height = newHeight;
		for (DynamicFrameBuffer buffer : buffers) {
			buffer.resetBuffer();
		}
	}

	private float scale;
	private FrameBuffer buffer;

	public DynamicFrameBuffer() {
		this(1.0f);
	}

	public DynamicFrameBuffer(float scale) {
		this.scale = scale;
		resetBuffer();
		buffers.add(this);
	}

	public void resetBuffer() {
		if (buffer != null) {
			buffer.dispose();
		}
		buffer = RenderUtils.makeFrameBuffer(Math.round(width * scale), Math.round(height * scale));
	}

	public void begin() {
		buffer.begin();
	}

	public void end() {
		buffer.end();
	}

	public Texture getTexture() {
		return buffer.getColorBufferTexture();
	}

	public void bindTexture() {
		buffer.getColorBufferTexture().bind();
	}
}