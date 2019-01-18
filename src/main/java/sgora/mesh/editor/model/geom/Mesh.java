package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.observables.ComplexObservable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mesh extends ComplexObservable {

	private final List<Point> nodes = new ArrayList<>();

	public void addNode(Point node) {
		node.setUnchanged();
		node.notifyManually = false;
		nodes.add(node);
		addSubObservable(node);
		onValueChanged();
	}

	public void removeNode(int nodeIndex) {
		nodes.remove(nodeIndex);
		onValueChanged();
	}

	public Point getNode(int index) {
		return nodes.get(index);
	}

	public List<Point> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

}
