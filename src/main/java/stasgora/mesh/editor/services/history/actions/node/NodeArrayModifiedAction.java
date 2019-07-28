package stasgora.mesh.editor.services.history.actions.node;

import stasgora.mesh.editor.interfaces.action.history.UserAction;
import stasgora.mesh.editor.model.geom.Point;

import java.util.function.Consumer;

public abstract class NodeArrayModifiedAction implements UserAction {
	protected double x, y;

	protected static Consumer<Point> addNode;
	protected static Consumer<Point> removeNode;

	public NodeArrayModifiedAction(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static void setNodeMethodReferences(Consumer<Point> addPoint, Consumer<Point> removePoint) {
		NodeArrayModifiedAction.addNode = addPoint;
		NodeArrayModifiedAction.removeNode = removePoint;
	}
}
