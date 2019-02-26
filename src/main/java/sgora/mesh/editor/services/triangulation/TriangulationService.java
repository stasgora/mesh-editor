package sgora.mesh.editor.services.triangulation;

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
	private TriangleUtils triangleUtils;

	public TriangulationService(SettableObservable<Mesh> mesh, NodeUtils nodeUtils, TriangleUtils triangleUtils) {
		this.mesh = mesh;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
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
			triangleUtils.bindTrianglesBothWays(newTriangles[i], 0, triangle.triangles[i], triangle);
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
				if (neighbour == null) {
					continue;
				}
				Point otherNode = triangleUtils.getSeparateNode(neighbour, current);
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
		double det = triangleUtils.D_matrixDet(current.nodes[nodeIndex], node, current.nodes[(nodeIndex + 1) % 3]);
		return det < 0 ? current.triangles[nodeIndex] : null;
	}

	private boolean isPointInsideCircumcircle(Point node, Triangle triangle) {
		return triangleUtils.H_matrixDet(triangle.nodes[2], triangle.nodes[1], triangle.nodes[0], node) > 0;
	}

	private Triangle[] flipTriangles(Triangle a, Triangle b) {
		Mesh mesh = this.mesh.get();
		int aNodeIndex = triangleUtils.getSeparateNodeIndex(a, b);
		int bNodeIndex = triangleUtils.getSeparateNodeIndex(b, a);
		mesh.removeTriangle(a);
		mesh.removeTriangle(b);
		Triangle[] added = new Triangle[2];
		added[0] = new Triangle(a.nodes[aNodeIndex], a.nodes[(aNodeIndex + 1) % 3], b.nodes[bNodeIndex]);
		added[1] = new Triangle(b.nodes[bNodeIndex], b.nodes[(bNodeIndex + 1) % 3], a.nodes[aNodeIndex]);
		triangleUtils.bindTrianglesBothWays(added[0], 0, a.triangles[aNodeIndex], a);
		triangleUtils.bindTrianglesBothWays(added[0], 1, b.triangles[(bNodeIndex + 2) % 3], b);
		triangleUtils.bindTrianglesBothWays(added[1], 0, b.triangles[bNodeIndex], b);
		triangleUtils.bindTrianglesBothWays(added[1], 1, a.triangles[(aNodeIndex + 2) % 3], a);
		added[0].triangles[2] = added[1];
		added[1].triangles[2] = added[0];
		mesh.addTriangle(added[0]);
		mesh.addTriangle(added[1]);
		return added;
	}

}
