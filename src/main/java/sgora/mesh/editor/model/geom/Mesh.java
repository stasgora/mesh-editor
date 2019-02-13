package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.paint.SerializableColor;
import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.SettableProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mesh extends ComplexObservable implements Serializable {

	private List<Point> nodes = new ArrayList<>();

	public SettableProperty<SerializableColor> nodeColor = new SettableProperty<>(new SerializableColor(0.1, 0.2, 1, 1));
	public SettableProperty<Integer> nodeRadius = new SettableProperty<>(8);

	public Mesh() {
		addSubObservable(nodeColor);
		addSubObservable(nodeRadius);
	}

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
		out.writeObject(nodeColor);
		out.writeObject(nodeRadius);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		nodes = (List<Point>) in.readObject();
		nodeColor = (SettableProperty<SerializableColor>) in.readObject();
		nodeRadius = (SettableProperty<Integer>) in.readObject();
		nodes.forEach(this::addSubObservable);
	}

}
