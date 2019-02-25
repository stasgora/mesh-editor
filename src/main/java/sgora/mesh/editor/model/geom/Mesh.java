package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.paint.SerializableColor;
import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.SettableProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Mesh extends ComplexObservable implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(Mesh.class.getName());

	private List<Point> nodes = new ArrayList<>();
	private List<Triangle> triangles = new ArrayList<>();
	private List<Triangle> validTriangles = new ArrayList<>();

	public final List<Point> boundingNodes;
	public Rectangle nodeBoundingBox;

	public SettableProperty<SerializableColor> nodeColor = new SettableProperty<>(new SerializableColor(0.1, 0.2, 1, 1));
	public SettableProperty<Integer> nodeRadius = new SettableProperty<>(8);

	public Mesh(Point[] boundingNodes, Rectangle nodeBoundingBox) {
		if(boundingNodes.length != 3) {
			LOGGER.warning("Mesh bounding nodes number wrong");
		}
		this.boundingNodes = Collections.unmodifiableList(Arrays.asList(boundingNodes));
		addTriangle(new Triangle(boundingNodes[0], boundingNodes[1], boundingNodes[2]));
		this.nodeBoundingBox = nodeBoundingBox;

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

	public Point getNode(int index) {
		return nodes.get(index);
	}

	public List<Point> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public void addTriangle(Triangle triangle) {
		triangles.add(triangle);
		onValueChanged();
	}

	public void removeTriangle(Triangle triangle) {
		triangles.remove(triangle);
		validTriangles.remove(triangle);
		onValueChanged();
	}

	public Triangle getTriangle(int index) {
		return triangles.get(index);
	}

	public List<Triangle> getTriangles() {
		return Collections.unmodifiableList(triangles);
	}

	public void addValidTriangle(Triangle triangle) {
		validTriangles.add(triangle);
	}

	public List<Triangle> getValidTriangles() {
		return Collections.unmodifiableList(validTriangles);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(nodes);
		out.writeObject(triangles);
		out.writeObject(nodeBoundingBox);
		out.writeObject(nodeColor);
		out.writeObject(nodeRadius);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		nodes = (List<Point>) in.readObject();
		triangles = (List<Triangle>) in.readObject();
		nodeBoundingBox = (Rectangle) in.readObject();
		nodeColor = (SettableProperty<SerializableColor>) in.readObject();
		nodeRadius = (SettableProperty<Integer>) in.readObject();

		nodes.forEach(this::addSubObservable);
		addSubObservable(nodeColor);
		addSubObservable(nodeRadius);
	}

}
