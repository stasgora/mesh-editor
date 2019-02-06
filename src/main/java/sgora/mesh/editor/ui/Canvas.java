package sgora.mesh.editor.ui;

import javafx.scene.canvas.GraphicsContext;

public class Canvas extends javafx.scene.canvas.Canvas {

	protected GraphicsContext context = getGraphicsContext2D();

	@Override
	public boolean isResizable() {
		return true;
	}

	public void clear() {
		context.clearRect(0, 0, getWidth(), getHeight());
	}

}
