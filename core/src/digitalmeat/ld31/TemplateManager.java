package digitalmeat.ld31;

import java.util.HashMap;
import java.util.Map;

import ch.digitalmeat.grid.util.Table;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class TemplateManager {

	private int width;
	private int height;

	private Map<String, Template> templates = new HashMap<String, Template>();

	public TemplateManager(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void loadTemplate(String key, String file) {
		Pixmap pixmap = new Pixmap(Gdx.files.internal(file));
		int endX = Math.min(width, pixmap.getWidth());
		int endY = Math.min(height, pixmap.getHeight());
		Template template = new Template(endX, endY);
		for (int y = 0; y < endY; y++) {
			for (int x = 0; x < endX; x++) {
				template.table.set(x, height - y - 1, new Color(pixmap.getPixel(x, y)));
			}
		}
		templates.put(key, template);
	}

	public Template getTemplate(String key) {
		return templates.get(key);
	}

	public static class Template {
		public final Table<Color> table;

		public Template(int width, int height) {
			this.table = new Table<Color>(width, height);
		}
	}
}
