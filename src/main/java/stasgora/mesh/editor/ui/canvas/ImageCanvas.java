package stasgora.mesh.editor.ui.canvas;

import io.github.stasgora.observetree.SettableProperty;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.project.CanvasData;

public class ImageCanvas extends ResizableCanvas {

	private CanvasData canvasData;
	private SettableProperty<Double> imageTransparency;

	public void init(CanvasData canvasData, SettableProperty<Double> imageTransparency) {
		this.canvasData = canvasData;
		this.imageTransparency = imageTransparency;
	}

	public void draw() {
		if (!isVisible()) {
			return;
		}
		double alpha = imageTransparency.get();
		context.setGlobalAlpha(alpha);
		Rectangle imageBox = canvasData.imageBox;
		context.drawImage(canvasData.baseImage.get(), imageBox.position.x, imageBox.position.y, imageBox.size.x, imageBox.size.y);
		context.setGlobalAlpha(1);
	}
}
