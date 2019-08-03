package stasgora.mesh.editor.ui.canvas;

import javafx.scene.image.Image;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.project.VisualProperties;

public class ImageCanvas extends Canvas {

	private VisualProperties visualProperties;

	public void init(VisualProperties visualProperties) {
		this.visualProperties = visualProperties;
	}

	public void draw(Rectangle imageBox, Image baseImage) {
		if(!isVisible()) {
			return;
		}
		double alpha = visualProperties.imageTransparency.get();
		context.setGlobalAlpha(alpha);
		context.drawImage(baseImage, imageBox.position.x, imageBox.position.y, imageBox.size.x,  imageBox.size.y);
		context.setGlobalAlpha(1);
	}
}
