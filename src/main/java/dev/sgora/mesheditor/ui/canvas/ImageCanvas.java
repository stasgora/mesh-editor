package dev.sgora.mesheditor.ui.canvas;

import dev.sgora.mesheditor.model.project.CanvasData;
import io.github.stasgora.observetree.SettableProperty;
import dev.sgora.mesheditor.model.geom.polygons.Rectangle;

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
		context.drawImage(canvasData.baseImage.get(), imageBox.getPosition().getX(), imageBox.getPosition().getY(), imageBox.getSize().getX(), imageBox.getSize().getY());
		context.setGlobalAlpha(1);
	}
}
