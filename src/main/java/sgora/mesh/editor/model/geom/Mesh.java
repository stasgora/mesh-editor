package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.observables.ComplexObservable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mesh extends ComplexObservable implements Serializable {

	private List<Point> nodes = new ArrayList<>();

	public void addNode(Point node) {
		nodes.add(node);
		addSubObservable(node);
		onValueChanged();
	}

	public void removeNode(int nodeIndex) {
		nodes.remove(nodeIndex);
		onValueChanged();
	}

	public void clear() {
		nodes.clear();
	}

	public Point getNode(int index) {
		return nodes.get(index);
	}

	public List<Point> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(nodes);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		nodes = (List<Point>) in.readObject();
		nodes.forEach(this::addSubObservable);
	}

}
