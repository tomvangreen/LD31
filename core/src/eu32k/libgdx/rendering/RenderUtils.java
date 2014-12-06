package eu32k.libgdx.rendering;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class RenderUtils {
	public static FrameBuffer makeFrameBuffer(int xRes, int yRes) {
		FrameBuffer frameBuffer = new FrameBuffer(Format.RGBA8888, xRes, yRes, false);
		frameBuffer.getColorBufferTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		frameBuffer.getColorBufferTexture().setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
		return frameBuffer;
	}
}