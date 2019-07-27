package stasgora.mesh.editor.services.history.actions;

import stasgora.mesh.editor.model.geom.Point;

public class RemovePointUserAction extends PointArrayModifiedUserAction {

	public RemovePointUserAction(double x, double y) {
		super(x, y);
	}

	@Override
	public void execute() {
		removePoint.accept(new Point(x, y));
	}

	@Override
	public void unExecute() {
		addPoint.accept(new Point(x, y));
	}
}
