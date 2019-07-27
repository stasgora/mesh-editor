package stasgora.mesh.editor.services.history.actions;

import stasgora.mesh.editor.model.geom.Point;

public class AddPointUserAction extends PointArrayModifiedUserAction {

	public AddPointUserAction(double x, double y) {
		super(x, y);
	}

	@Override
	public void execute() {
		addPoint.accept(new Point(x, y));
	}

	@Override
	public void unExecute() {
		removePoint.accept(new Point(x, y));
	}
}
