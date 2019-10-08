package dev.sgora.mesheditor.services.input;

import dev.sgora.mesheditor.model.geom.Point;
import javafx.scene.input.MouseButton;

public interface MouseListener {

	boolean onDragStart(Point mousePos, MouseButton button);

	void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button);

	void onDragEnd(Point mousePos, MouseButton button);

}
