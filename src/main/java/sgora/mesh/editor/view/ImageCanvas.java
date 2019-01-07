package sgora.mesh.editor.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import sgora.mesh.editor.model.Rectangle;

public class ImageCanvas extends Canvas {

	GraphicsContext gc = getGraphicsContext2D();

	public void drawImage(Image image, Rectangle imageBox) {
		if(!isVisible() || image == null)
			return;
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.drawImage(image, imageBox.getPosition().x, imageBox.getPosition().y, imageBox.getSize().x, imageBox.getSize().y);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

}
