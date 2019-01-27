package sgora.mesh.editor.model.interfaces;

import javafx.scene.input.MouseButton;
import sgora.mesh.editor.model.geom.Point;

public interface MouseListener {

	void onDragStart(Point mousePos, MouseButton button);
	void onMouseDrag(Point mousePos, MouseButton button);
	void onDragEnd(Point mousePos, MouseButton button);

	void onZoom(double amount, Point mousePos);

	void onMouseEnter(boolean isDragging);
	void onMouseExit(boolean isDragging);

}
