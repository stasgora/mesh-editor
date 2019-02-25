package sgora.mesh.editor.triangulation;

import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.observables.SettableObservable;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

public class TriangulationService {

	private static final Logger LOGGER = Logger.getLogger(TriangulationService.class.getName());

	private SettableObservable<Mesh> mesh;
	private NodeUtils nodeUtils;

	public TriangulationService(SettableObservable<Mesh> mesh, NodeUtils nodeUtils) {
		this.mesh = mesh;
		this.nodeUtils = nodeUtils;
	}

	public void createNewMesh() {
		Mesh mesh = new Mesh(nodeUtils.getBoundingNodes());
		this.mesh.set(mesh);
	}

	public void addNode(Point node) {
		Mesh mesh = this.mesh.get();
		Triangle triangle = walkToContainerTriangle(node);
		mesh.removeTriangle(triangle);
		Triangle[] newTriangles = new Triangle[3];
		for (int i = 0; i < 3; i++) {
			newTriangles[i] = new Triangle(triangle.nodes[i], triangle.nodes[(i + 1) % 3], node);
			bindTrianglesBothWays(newTriangles[i], 0, triangle.triangles[i], triangle);
		}
		Stack<Triangle> trianglesToCheck = new Stack<>();
		for (int i = 0; i < 3; i++) {
			newTriangles[i].triangles[1] = newTriangles[(i + 1) % 3];
			newTriangles[i].triangles[2] = newTriangles[(i + 2) % 3];
			addTriangle(newTriangles[i]);
			trianglesToCheck.push(newTriangles[i]);
		}
		flipInvalidTriangles(trianglesToCheck);
		mesh.addNode(node);
		mesh.notifyListeners();
	}

	private void flipInvalidTriangles(Stack<Triangle> remaining) {
		while (!remaining.empty()) {
			Triangle current = remaining.pop();
			for (Triangle neighbour : current.triangles) {
				if (neighbour == null) {
					continue;
				}
				Point otherNode = getSeparateNode(neighbour, current);
				if (isPointInsideCircumcircle(otherNode, current)) {
					Triangle[] created = flipTriangles(current, neighbour);
					remaining.addAll(Arrays.asList(created));
				}
			}
		}
	}

	private Triangle walkToContainerTriangle(Point node) {
		Triangle current = mesh.get().getTriangle(0);
		Triangle next = null;
		do {
			for (int i = 0; i < 3; i++) {
				next = getCloserTriangle(node, current, i);
				if (next != null) {
					current = next;
					break;
				}
			}
		} while (next != null);
		return current;
	}

	private Triangle getCloserTriangle(Point node, Triangle current, int nodeIndex) {
		double det = D_matrixDet(current.nodes[nodeIndex], node, current.nodes[(nodeIndex + 1) % 3]);
		return det < 0 ? current.triangles[nodeIndex] : null;
	}

	private boolean isPointInsideCircumcircle(Point node, Triangle triangle) {
		return H_matrixDet(triangle.nodes[2], triangle.nodes[1], triangle.nodes[0], node) > 0;
	}

	private Point getSeparateNode(Triangle from, Triangle with) {
		return from.nodes[getSeparateNodeIndex(from, with)];
	}

	private int getSeparateNodeIndex(Triangle from, Triangle with) {
		List<Point> nodes = Arrays.asList(with.nodes);
		for (int i = 0; i < from.nodes.length; i++) {
			if (!nodes.contains(from.nodes[i])) {
				return i;
			}
		}
		LOGGER.warning("No separate node found");
		return -1;
	}

	private void addTriangle(Triangle triangle) {
		List<Point> boundingNodes = mesh.get().boundingNodes;
		boolean isBoundingTriangle = false;
		for (Point node : triangle.nodes) {
			if (boundingNodes.contains(node)) {
				isBoundingTriangle = true;
				break;
			}
		}
		if (!isBoundingTriangle) {
			//mesh.get().addValidTriangle(triangle);
		}
		mesh.get().addTriangle(triangle);
	}

	private void bindTrianglesBothWays(Triangle a, int aIndex, Triangle b, Triangle bIndex) {
		a.triangles[aIndex] = b;
		if(b == null) {
			return;
		}
		int index = Arrays.asList(b.triangles).indexOf(bIndex);
		if(index == -1) {
			LOGGER.warning("Triangle " + bIndex + " is not an neighbour of " + b);
		}
		b.triangles[index] = a;
	}

	private Triangle[] flipTriangles(Triangle a, Triangle b) {
		Mesh mesh = this.mesh.get();
		int aNodeIndex = getSeparateNodeIndex(a, b);
		int bNodeIndex = getSeparateNodeIndex(b, a);
		mesh.removeTriangle(a);
		mesh.removeTriangle(b);
		Triangle[] added = new Triangle[2];
		added[0] = new Triangle(a.nodes[aNodeIndex], a.nodes[(aNodeIndex + 1) % 3], b.nodes[bNodeIndex]);
		added[1] = new Triangle(b.nodes[bNodeIndex], b.nodes[(bNodeIndex + 1) % 3], a.nodes[aNodeIndex]);
		bindTrianglesBothWays(added[0], 0, a.triangles[aNodeIndex], a);
		bindTrianglesBothWays(added[0], 1, b.triangles[(bNodeIndex + 2) % 3], b);
		bindTrianglesBothWays(added[1], 0, b.triangles[bNodeIndex], b);
		bindTrianglesBothWays(added[1], 1, a.triangles[(aNodeIndex + 2) % 3], a);
		added[0].triangles[2] = added[1];
		added[1].triangles[2] = added[0];
		addTriangle(added[0]);
		addTriangle(added[1]);
		return added;
	}

	private double D_matrixDet(Point a, Point b, Point c) {
		return a.x * b.y + a.y * c.x + b.x * c.y - a.y * b.x - b.y * c.x - c.y * a.x;
	}

	private double H_matrixDet(Point a, Point b, Point c, Point d) {
		return a.x * a.x * b.x * c.y - a.x * a.x * b.x * d.y + a.x * a.x * (-b.y) * c.x + a.x * a.x * b.y * d.x + a.x * a.x * c.x * d.y - a.x * a.x * c.y * d.x
				- a.x * b.x * b.x * c.y + a.x * b.x * b.x * d.y - a.x * b.y * b.y * c.y + a.x * b.y * b.y * d.y + a.x * b.y * c.x * c.x + a.x * b.y * c.y * c.y
				- a.x * b.y * d.x * d.x - a.x * b.y * d.y * d.y - a.x * c.x * c.x * d.y - a.x * c.y * c.y * d.y + a.x * c.y * d.x * d.x + a.x * c.y * d.y * d.y
				+ a.y * a.y * b.x * c.y - a.y * a.y * b.x * d.y - a.y * a.y * b.y * c.x + a.y * a.y * b.y * d.x + a.y * a.y * c.x * d.y - a.y * a.y * c.y * d.x
				+ a.y * b.x * b.x * c.x - a.y * b.x * b.x * d.x - a.y * b.x * c.x * c.x - a.y * b.x * c.y * c.y + a.y * b.x * d.x * d.x + a.y * b.x * d.y * d.y
				+ a.y * b.y * b.y * c.x - a.y * b.y * b.y * d.x + a.y * c.x * c.x * d.x - a.y * c.x * d.x * d.x - a.y * c.x * d.y * d.y + a.y * c.y * c.y * d.x
				- b.x * b.x * c.x * d.y + b.x * b.x * c.y * d.x + b.x * c.x * c.x * d.y + b.x * c.y * c.y * d.y - b.x * c.y * d.x * d.x - b.x * c.y * d.y * d.y
				- b.y * b.y * c.x * d.y + b.y * b.y * c.y * d.x - b.y * c.x * c.x * d.x + b.y * c.x * d.x * d.x + b.y * c.x * d.y * d.y - b.y * c.y * c.y * d.x;
	}

}
