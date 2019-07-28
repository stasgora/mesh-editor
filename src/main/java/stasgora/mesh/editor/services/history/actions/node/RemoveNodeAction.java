package stasgora.mesh.editor.services.history.actions.node;

import stasgora.mesh.editor.model.geom.Point;

public class RemoveNodeAction extends NodeArrayModifiedAction {

	public RemoveNodeAction(double x, double y) {
		super(x, y);
	}

	@Override
	public void execute() {
		removeNode.accept(new Point(x, y));
	}

	@Override
	public void unExecute() {
		addNode.accept(new Point(x, y));
	}
}
