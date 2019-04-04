package sgora.mesh.editor.interfaces;

import javafx.scene.input.MouseButton;
import sgora.mesh.editor.model.geom.Point;

public interface MouseListener {

	boolean onDragStart(Point mousePos, MouseButton button);
	boolean onMouseDrag(Point dragAmount, Point mousePos, MouseButton button);
	boolean onDragEnd(Point mousePos, MouseButton button);

}
