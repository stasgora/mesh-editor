package stasgora.mesh.editor.services.mesh.triangulation;

import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import io.github.stasgora.observetree.SettableObservable;

import java.util.*;

public class FlipBasedTriangulationService implements TriangulationService {

	private SettableObservable<Mesh> mesh;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private FlippingUtils flippingUtils;

	public FlipBasedTriangulationService(SettableObservable<Mesh> mesh, NodeUtils nodeUtils, TriangleUtils triangleUtils, FlippingUtils flippingUtils) {
		this.mesh = mesh;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
		this.flippingUtils = flippingUtils;
	}

	@Override
	public void createNewMesh() {
		Mesh mesh = new Mesh(nodeUtils.getBoundingNodes());
		this.mesh.set(mesh);
	}

	@Override
	public boolean addNode(Point location) {
		Mesh mesh = this.mesh.get();
		Triangle triangle = triangleUtils.findTriangleByLocation(location);
		if(nodeUtils.getClosestNode(location, triangle) != null) {
			return false;
		}
		mesh.removeTriangle(triangle);
		Triangle[] newTriangles = new Triangle[3];
		for (int i = 0; i < 3; i++) {
			newTriangles[i] = new Triangle(triangle.nodes[i], triangle.nodes[(i + 1) % 3], location);
			triangleUtils.bindTrianglesBothWays(newTriangles[i], 0, triangle.triangles[i], triangle);
		}
		Stack<Triangle> trianglesToCheck = new Stack<>();
		for (int i = 0; i < 3; i++) {
			newTriangles[i].triangles[1] = newTriangles[(i + 1) % 3];
			newTriangles[i].triangles[2] = newTriangles[(i + 2) % 3];
			mesh.addTriangle(newTriangles[i]);
			trianglesToCheck.push(newTriangles[i]);
		}
		flippingUtils.flipInvalidTriangles(trianglesToCheck);
		mesh.addNode(location);
		mesh.notifyListeners();
		return true;
	}

	@Override
	public Point findNodeByLocation(Point location) {
		Triangle triangle = triangleUtils.findTriangleByLocation(location);
		return nodeUtils.getClosestNode(location, triangle);
	}

	@Override
	public boolean removeNode(Point location) {
		Triangle triangle = triangleUtils.findTriangleByLocation(location);
		Point node = nodeUtils.getClosestNode(location, triangle);
		if(node == null) {
			return false;
		}
		List<Point> points = new ArrayList<>();
		List<Triangle> triangles = new ArrayList<>();
		nodeUtils.getNodeNeighbours(node, triangle, points, triangles);
		retriangulateNodeHole(node, points, triangles);
		mesh.get().notifyListeners();
		return true;
	}

	@Override
	public Point moveNode(Point node, Point position) {
		Triangle triangle = triangleUtils.findTriangleByLocation(node);
		List<Point> points = new ArrayList<>();
		List<Triangle> triangles = new ArrayList<>();
		nodeUtils.getNodeNeighbours(node, triangle, points, triangles);

		node.set(position);
		Stack<Triangle> trianglesToCheck = new Stack<>();
		trianglesToCheck.addAll(triangles);
		flippingUtils.flipInvalidTriangles(trianglesToCheck);
		mesh.get().notifyListeners();
		return node;
	}

	private void retriangulateNodeHole(Point node, List<Point> nodes, List<Triangle> triangles) {
		Mesh mesh = this.mesh.get();
		Point[] currentNodes = new Point[3];
		currentNodes[0] = nodes.get(0);
		while (nodes.size() > 3) {
			int currentId = nodes.indexOf(currentNodes[0]);
			currentNodes[1] = nodes.get((currentId + 1) % nodes.size());
			currentNodes[2] = nodes.get((currentId + 2) % nodes.size());
			double earTest = triangleUtils.D_matrixDet(currentNodes[2], currentNodes[1], currentNodes[0]);
			double enclosingTest = triangleUtils.D_matrixDet(currentNodes[0], node, currentNodes[2]);
			if (earTest >= 0 && enclosingTest >= 0 && checkTriangleAgainstNodes(nodes, currentNodes, currentId)) {
				flippingUtils.flipTrianglesFromRing(node, nodes, triangles, currentId);
			} else {
				currentNodes[0] = nodes.get((currentId + 1) % nodes.size());
			}
		}
		triangleUtils.mergeTrianglesIntoOne(nodes, triangles);
		mesh.removeNode(node);
	}

	private boolean checkTriangleAgainstNodes(List<Point> nodes, Point[] currentNodes, int currentId) {
		for (int i = 0; i < nodes.size() - 3; i++) {
			int index = (currentId + 3 + i) % nodes.size();
			double circumcircleTest = triangleUtils.H_matrixDet(currentNodes[2], currentNodes[1], currentNodes[0], nodes.get(index));
			if(circumcircleTest > 0) {
				return false;
			}
		}
		return true;
	}

}
