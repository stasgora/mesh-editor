package sgora.mesh.editor.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import sgora.mesh.editor.model.containers.ImageBoxModel;
import sgora.mesh.editor.model.geom.Rectangle;

public class ImageCanvas extends Canvas {

	private GraphicsContext gc = getGraphicsContext2D();

	public void draw(ImageBoxModel imageBox) {
		if(!isVisible() || imageBox.baseImage == null)
			return;
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.drawImage(imageBox.baseImage, imageBox.imageBox.getPosition().x, imageBox.imageBox.getPosition().y, imageBox.imageBox.getSize().x, imageBox.imageBox.getSize().y);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

}
