package stasgora.mesh.editor.services.history.actions.node;

import stasgora.mesh.editor.model.geom.Point;

public class MoveNodeAction extends NodeModifiedAction {
	private Point point;
	private double oldX;
	private double oldY;

	public MoveNodeAction(Point movedPoint, Point oldPoint) {
		super(movedPoint.getX(), movedPoint.getY());
		this.point = movedPoint;
		this.oldX = oldPoint.getX();
		this.oldY = oldPoint.getY();
	}

	@Override
	public void execute() {
		switchNodes(x, y);
	}

	@Override
	public void unExecute() {
		switchNodes(oldX, oldY);
	}

	private void switchNodes(double x, double y) {
		removeNode.accept(point);
		point = new Point(x, y);
		addNode.accept(point);
	}
}
