package sgora.mesh.editor.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import sgora.mesh.editor.controller.ImageBoxController;
import sgora.mesh.editor.model.Point;
import sgora.mesh.editor.model.Rectangle;

public class CanvasView extends Canvas {

	private boolean drawImage = true;

	public void draw(Rectangle imageBox, Image baseImage) {
		if(baseImage == null)
			return;

		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());

		if(drawImage) {
			gc.drawImage(baseImage, imageBox.getPosition().x, imageBox.getPosition().y, imageBox.getSize().x, imageBox.getSize().y);
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

}
