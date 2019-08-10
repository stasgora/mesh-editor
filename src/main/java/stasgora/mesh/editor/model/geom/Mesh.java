package stasgora.mesh.editor.model.geom;

import io.github.stasgora.observetree.Observable;
import stasgora.mesh.editor.model.geom.polygons.Polygon;
import stasgora.mesh.editor.model.geom.polygons.Triangle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Mesh extends Observable implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(Mesh.class.getName());
	private static final long serialVersionUID = 1L;

	private List<PointRegion> nodeRegions = new ArrayList<>();
	private List<Triangle> triangles = new ArrayList<>();

	public List<Point> boundingNodes;

	public Mesh(Point[] boundingNodes) {
		if(boundingNodes.length != 3) {
			LOGGER.warning("Mesh bounding nodes number wrong");
		}
		this.boundingNodes = List.of(boundingNodes);
		addTriangle(new Triangle(boundingNodes[0], boundingNodes[1], boundingNodes[2]));
	}

	public void addNode(Point node) {
		nodeRegions.add(new PointRegion(node));
		addSubObservable(node);
		onValueChanged();
	}

	public Polygon getPointRegion(Point node) {
		for (PointRegion pointRegion : nodeRegions) {
			if (pointRegion.node == node)
				return pointRegion.region;
		}
		return null;
	}

	public void removeNode(Point node) {
		for (int i = 0; i < nodeRegions.size(); i++) {
			if(nodeRegions.get(i).node == node) {
				nodeRegions.remove(i);
				break;
			}
		}
		onValueChanged();
	}

	public List<Point> getNodes() {
		return nodeRegions.stream().map(pointRegion -> pointRegion.node).collect(Collectors.toUnmodifiableList());
	}

	public List<PointRegion> getNodeRegions() {
		return Collections.unmodifiableList(nodeRegions);
	}

	public void addTriangle(Triangle triangle) {
		triangles.add(triangle);
		onValueChanged();
	}

	public void removeTriangle(Triangle triangle) {
		triangles.remove(triangle);
		onValueChanged();
	}

	public Triangle getTriangle(int index) {
		return triangles.get(index);
	}

	public List<Triangle> getTriangles() {
		return Collections.unmodifiableList(triangles);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		for (int i = 0; i < triangles.size(); i++) {
			triangles.get(i).triangleId = i;
		}
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		nodeRegions.forEach(pointRegion -> addSubObservable(pointRegion.node));
		triangles.forEach(this::assignTriangleNeighbours);
	}

	private void assignTriangleNeighbours(Triangle triangle) {
		triangle.triangles = new Triangle[3];
		for (int i = 0; i < 3; i++) {
			if(triangle.triangleIds[i] >= 0) {
				triangle.triangles[i] = triangles.get(triangle.triangleIds[i]);
			}
		}
	}

}
