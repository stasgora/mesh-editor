package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.ObservableModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mesh extends ObservableModel {

	private final List<Point> nodes = new ArrayList<>();

	public void addNode(Point node) {
		nodes.add(node);
		onValueChanged();
	}

	public void removeNode(int nodeIndex) {
		nodes.remove(nodeIndex);
		onValueChanged();
	}

	public List<Point> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

}
