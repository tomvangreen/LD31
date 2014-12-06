package eu32k.libgdx.rendering;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Primitives {

	private static Mesh makeQuad(boolean inverted) {
		Mesh quad = new Mesh(true, 4, 0, new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
		quad.setVertices(new float[] { -1f, 1f, 0f, 0f, inverted ? 1f : 0f, -1f, -1, 0f, 0f, inverted ? 0f : 1f, 1f, -1f, 0f, 1f, inverted ? 0f : 1f, 1f, 1f, 0f, 1f, inverted ? 1f : 0f });
		return quad;
	}

	public static Mesh QUAD_NORMAL = makeQuad(false);
	public static Mesh QUAD_FLIPPED = makeQuad(true);
}