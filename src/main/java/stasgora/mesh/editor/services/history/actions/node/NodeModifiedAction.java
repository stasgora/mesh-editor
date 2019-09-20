package stasgora.mesh.editor.services.history.actions.node;

import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.services.history.actions.UserAction;

import java.util.function.Consumer;

public abstract class NodeModifiedAction implements UserAction {
	protected double x;
	protected double y;

	protected static Consumer<Point> addNode;
	protected static Consumer<Point> removeNode;

	public NodeModifiedAction(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static void setNodeMethodReferences(Consumer<Point> addPoint, Consumer<Point> removePoint) {
		NodeModifiedAction.addNode = addPoint;
		NodeModifiedAction.removeNode = removePoint;
	}
}
