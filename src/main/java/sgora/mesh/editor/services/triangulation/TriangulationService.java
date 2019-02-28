package sgora.mesh.editor.services.triangulation;

import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.observables.SettableObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class TriangulationService {

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

	public void removeNode(Point location) {
		Triangle triangle = walkToContainerTriangle(location);
		Point node = nodeUtils.getClosestNode(location, triangle);
		if(node == null) {
			return;
		}
		List<Point> points = new ArrayList<>();
		List<Triangle> triangles = new ArrayList<>();
		nodeUtils.getNodeNeighbours(node, triangle, points, triangles);
		retriangulateWithoutCenterNode(node, points, triangles);
	}

	private void retriangulateWithoutCenterNode(Point node, List<Point> nodes, List<Triangle> triangles) {
		Mesh mesh = this.mesh.get();
		Point[] currentNodes = new Point[3];
		currentNodes[0] = nodes.get(0);
		while (nodes.size() > 3) {
			int currentId = nodes.indexOf(currentNodes[0]);
			currentNodes[1] = nodes.get((currentId + 1) % nodes.size());
			currentNodes[2] = nodes.get((currentId + 2) % nodes.size());
			double earTest = triangleUtils.D_matrixDet(currentNodes[0], currentNodes[1], currentNodes[2]);
			double enclosingTest = triangleUtils.D_matrixDet(currentNodes[0], currentNodes[2], node);
			if (earTest >= 0 && enclosingTest >= 0 && checkTriangleAgainstNodes(nodes, currentNodes, currentId)) {
				flipTrianglesFromRing(node, nodes, triangles, currentId);
			} else {
				currentNodes[0] = nodes.get((currentId + 1) % nodes.size());
			}
		}
		triangleUtils.mergeTrianglesIntoOne(nodes, triangles);
		mesh.removeNode(node);
	}

	private void flipTrianglesFromRing(Point node, List<Point> nodes, List<Triangle> triangles, int currentId) {
		int nextId = (currentId + 1) % nodes.size();
		Triangle[] currentTriangles = new Triangle[] { triangles.get(currentId), triangles.get(nextId) };
		Triangle[] newTriangles = flipTriangles(currentTriangles[0], currentTriangles[1]);
		Triangle newNeighbour = Arrays.asList(newTriangles[0].nodes).contains(node) ? newTriangles[0] : newTriangles[1];
		nodes.remove(nextId);
		triangles.add(currentId, newNeighbour);
		triangles.remove(currentTriangles[0]);
		triangles.remove(currentTriangles[1]);
	}

	private boolean checkTriangleAgainstNodes(List<Point> nodes, Point[] currentNodes, int currentId) {
		for (int i = 0; i < nodes.size() - 3; i++) {
			int index = (currentId + 3 + i) % nodes.size();
			double circumcircleTest = triangleUtils.H_matrixDet(currentNodes[0], currentNodes[1], currentNodes[2], nodes.get(index));
			if(circumcircleTest > 0) {
				return false;
			}
		}
		return true;
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
