package stasgora.mesh.editor.services.history.actions;

import stasgora.mesh.editor.interfaces.action.history.UserAction;
import stasgora.mesh.editor.model.geom.Point;

import java.util.function.Consumer;

public abstract class PointArrayModifiedUserAction implements UserAction {
	protected double x, y;

	protected static Consumer<Point> addPoint;
	protected static Consumer<Point> removePoint;

	public PointArrayModifiedUserAction(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static void setPointMethodReferences(Consumer<Point> addPoint, Consumer<Point> removePoint) {
		PointArrayModifiedUserAction.addPoint = addPoint;
		PointArrayModifiedUserAction.removePoint = removePoint;
	}
}
