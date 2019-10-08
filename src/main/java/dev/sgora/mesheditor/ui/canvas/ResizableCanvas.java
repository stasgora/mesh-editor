package dev.sgora.mesheditor.ui.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class ResizableCanvas extends Canvas {

	protected GraphicsContext context = getGraphicsContext2D();

	@Override
	public boolean isResizable() {
		return true;
	}

	public void clear() {
		context.clearRect(0, 0, getWidth(), getHeight());
	}

}
