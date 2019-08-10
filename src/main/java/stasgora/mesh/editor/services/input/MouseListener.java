package stasgora.mesh.editor.services.input;

import javafx.scene.input.MouseButton;
import stasgora.mesh.editor.model.geom.Point;

public interface MouseListener {

	boolean onDragStart(Point mousePos, MouseButton button);

	void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button);

	void onDragEnd(Point mousePos, MouseButton button);

}
