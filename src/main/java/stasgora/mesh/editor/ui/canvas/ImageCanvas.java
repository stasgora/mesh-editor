package stasgora.mesh.editor.ui.canvas;

import io.github.stasgora.observetree.SettableProperty;
import javafx.scene.image.Image;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.project.VisualProperties;

public class ImageCanvas extends Canvas {

	private SettableProperty<Double> imageTransparency;

	public void init(SettableProperty<Double> imageTransparency) {
		this.imageTransparency = imageTransparency;
	}

	public void draw(Rectangle imageBox, Image baseImage) {
		if(!isVisible()) {
			return;
		}
		double alpha = imageTransparency.get();
		context.setGlobalAlpha(alpha);
		context.drawImage(baseImage, imageBox.position.x, imageBox.position.y, imageBox.size.x,  imageBox.size.y);
		context.setGlobalAlpha(1);
	}
}
