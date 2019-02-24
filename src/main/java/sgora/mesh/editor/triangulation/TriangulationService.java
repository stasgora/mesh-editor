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

	public TriangulationService(SettableObservable<Mesh> mesh) {
		this.mesh = mesh;
	}

	public void createNewMesh() {
		mesh.set(new Mesh());
	}

	public void addNode(Point node) {
		Mesh mesh = this.mesh.get();
		Triangle triangle = walkToContainerTriangle(node);
		mesh.removeTriangle(triangle);
		Triangle[] newTriangles = new Triangle[3];
		for (int i = 0; i < 3; i++) {
			newTriangles[i] = new Triangle(triangle.nodes[i], triangle.nodes[(i + 1) % 3], node);
			newTriangles[i].triangles[0] = triangle.triangles[i];
		}
		Stack<Triangle> trianglesToCheck = new Stack<>();
		for (int i = 0; i < 3; i++) {
			newTriangles[i].triangles[1] = newTriangles[(i + 1) % 3];
			newTriangles[i].triangles[2] = newTriangles[(i + 2) % 3];
			mesh.addTriangle(newTriangles[i]);
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
				Point otherNode = getSeparateNode(current, neighbour);
				if(isPointInsideCircumcircle(otherNode, current)) {
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
		double det = D_matrixDet(current.nodes[nodeIndex], current.nodes[(nodeIndex + 1) % 3], node);
		return det < 0 ? current.triangles[nodeIndex] : null;
	}

	private boolean isPointInsideCircumcircle(Point node, Triangle triangle) {
		return H_matrixDet(triangle.nodes[0], triangle.nodes[1], triangle.nodes[2], node) > 0;
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

	private Triangle[] flipTriangles(Triangle a, Triangle b) {
		Mesh mesh = this.mesh.get();
		int aNodeIndex = getSeparateNodeIndex(a, b);
		int bNodeIndex = getSeparateNodeIndex(b, a);
		mesh.removeTriangle(a);
		mesh.removeTriangle(b);
		Triangle[] added = new Triangle[2];
		added[0] = new Triangle(a.nodes[aNodeIndex], a.nodes[(aNodeIndex + 1) % 3], b.nodes[bNodeIndex]);
		added[1] = new Triangle(b.nodes[bNodeIndex], b.nodes[(bNodeIndex + 1) % 3], a.nodes[aNodeIndex]);
		added[0].triangles = new Triangle[]{a.triangles[aNodeIndex], b.triangles[(bNodeIndex + 2) % 3], added[1]};
		added[1].triangles = new Triangle[]{b.triangles[bNodeIndex], a.triangles[(aNodeIndex + 2) % 3], added[0]};
		mesh.addTriangle(added[0]);
		mesh.addTriangle(added[1]);
		return added;
	}

	private double D_matrixDet(Point a, Point b, Point c) {
		return a.x * b.y + a.y * c.x + b.x * c.y - a.y * b.x - b.y * c.x - c.y * a.x;
	}

	private double H_matrixDet(Point a, Point b, Point c, Point d) {
		return b.x * (a.x * a.x * (c.y - d.y) + b.x * (a.x * (d.y - c.y) + a.y * (c.x - d.x) - c.x * d.y + c.y * d.x) + a.y * (a.y * (c.y - d.y) - c.x * c.x
				- c.y * c.y + d.x * d.x + d.y * d.y) + c.x * c.x * d.y - c.y * d.x * d.x + c.y * (c.y * d.y - d.y * d.y)) + b.y * (b.y * (a.x * (d.y - c.y)
				+ a.y * (c.x - d.x) - c.x * d.y + c.y * d.x) + a.x * (a.x * (d.x - c.x) + c.x * c.x + c.y * c.y - d.x * d.x - d.y * d.y) + a.y * a.y * (d.x
				- c.x) + d.x * (-c.x * c.x + c.x * d.x - c.y * c.y) + c.x * d.y * d.y) + a.x * (a.x * (c.x * d.y - c.y * d.x) + c.x * c.x * (-d.y) + c.y * d.x
				* d.x + c.y * (d.y * d.y - c.y * d.y)) + a.y * (a.y * (c.x * d.y - c.y * d.x) + d.x * (c.x * c.x - c.x * d.x + c.y * c.y) - c.x * d.y * d.y);
	}

}
