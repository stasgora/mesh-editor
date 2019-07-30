package stasgora.mesh.editor.services.history.actions.node;

import stasgora.mesh.editor.model.geom.Point;

public class AddNodeAction extends NodeModifiedAction {

	public AddNodeAction(double x, double y) {
		super(x, y);
	}

	@Override
	public void execute() {
		addNode.accept(new Point(x, y));
	}

	@Override
	public void unExecute() {
		removeNode.accept(new Point(x, y));
	}
}
