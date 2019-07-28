package stasgora.mesh.editor.services.history.actions.node;

import stasgora.mesh.editor.interfaces.action.history.UserAction;
import stasgora.mesh.editor.model.geom.Point;

import java.util.function.BiConsumer;

public class MoveNodeAction implements UserAction {
	private Point movedPoint;
	private double oldX, oldY;
	private double newX, newY;

	private static BiConsumer<Point, Point> moveNode;

	public MoveNodeAction(Point movedPoint, Point oldPoint) {
		this.movedPoint = movedPoint;
		this.oldX = oldPoint.x;
		this.oldY = oldPoint.y;
		this.newX = movedPoint.x;
		this.newY = movedPoint.y;
	}

	@Override
	public void execute() {
		moveNode.accept(movedPoint, new Point(this.newX, this.newY));
	}

	@Override
	public void unExecute() {
		moveNode.accept(movedPoint, new Point(this.oldX, this.oldY));
	}

	public static void setMoveNode(BiConsumer<Point, Point> moveNode) {
		MoveNodeAction.moveNode = moveNode;
	}
}
