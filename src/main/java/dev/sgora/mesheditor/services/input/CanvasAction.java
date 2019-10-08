package dev.sgora.mesheditor.services.input;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public interface CanvasAction {
	void onMousePress(MouseEvent event);

	void onMouseDrag(MouseEvent event);

	void onMouseRelease(MouseEvent event);

	void onScroll(ScrollEvent event);

	void onMouseEnter(MouseEvent event);

	void onMouseExit(MouseEvent event);

	void onMouseMove(MouseEvent event);
}
