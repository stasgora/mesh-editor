package dev.sgora.mesheditor.services.history.actions.node;

import dev.sgora.mesheditor.model.geom.Point;

public class RemoveNodeAction extends NodeModifiedAction {

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
