package stasgora.mesh.editor.services.mesh.generation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.stasgora.observetree.SettableObservable;
import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.project.CanvasData;

import java.util.*;

@Singleton
class FlipBasedTriangulationService implements TriangulationService {

	private SettableObservable<Mesh> mesh;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private FlippingUtils flippingUtils;
	private VoronoiDiagramService voronoiDiagramService;

	@Inject
	FlipBasedTriangulationService(CanvasData canvasData, NodeUtils nodeUtils, TriangleUtils triangleUtils,
	                                     FlippingUtils flippingUtils, VoronoiDiagramService voronoiDiagramService) {
		this.mesh = canvasData.mesh;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
		this.flippingUtils = flippingUtils;
		this.voronoiDiagramService = voronoiDiagramService;
	}

	@Override
	public void createNewMesh() {
		mesh.set(new Mesh(nodeUtils.getBoundingNodes()));
	}

	@Override
	public boolean addNode(Point location) {
		Triangle triangle = triangleUtils.findTriangleByLocation(location);
		if (nodeUtils.getClosestNode(location, triangle) != null) {
			return false;
		}
		mesh.get().removeTriangle(triangle);
		Triangle[] newTriangles = new Triangle[3];
		for (int i = 0; i < 3; i++) {
			newTriangles[i] = new Triangle(triangle.getNodes()[i], triangle.getNodes()[(i + 1) % 3], location);
			triangleUtils.bindTrianglesBothWays(newTriangles[i], 0, triangle.getTriangles()[i], triangle);
		}
		Deque<Triangle> trianglesToCheck = new ArrayDeque<>();
		for (int i = 0; i < 3; i++) {
			newTriangles[i].getTriangles()[1] = newTriangles[(i + 1) % 3];
			newTriangles[i].getTriangles()[2] = newTriangles[(i + 2) % 3];
			mesh.get().addTriangle(newTriangles[i]);
			trianglesToCheck.push(newTriangles[i]);
		}
		List<Triangle> changedTriangles = flippingUtils.flipInvalidTriangles(trianglesToCheck);
		changedTriangles.addAll(Arrays.asList(newTriangles));
		mesh.get().addNode(location);

		voronoiDiagramService.generateDiagram(triangleUtils.getTrianglePointSet(changedTriangles));
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
		if (node == null) {
			return false;
		}
		List<Point> points = new ArrayList<>();
		List<Triangle> triangles = new ArrayList<>();
		nodeUtils.getNodeNeighbours(node, triangle, points, triangles);
		List<Point> neighbourPoints = new ArrayList<>(points);
		retriangulateNodeHole(node, points, triangles);
		mesh.get().removeNode(node);

		voronoiDiagramService.generateDiagram(neighbourPoints);
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
		Deque<Triangle> trianglesToCheck = new ArrayDeque<>(triangles);
		flippingUtils.flipInvalidTriangles(trianglesToCheck);

		points.add(node);
		voronoiDiagramService.generateDiagram(points);
		mesh.get().notifyListeners();
		return node;
	}

	private void retriangulateNodeHole(Point node, List<Point> nodes, List<Triangle> triangles) {
		Point[] currentNodes = new Point[3];
		currentNodes[0] = nodes.get(0);
		while (nodes.size() > 3) {
			int currentId = nodes.indexOf(currentNodes[0]);
			currentNodes[1] = nodes.get((currentId + 1) % nodes.size());
			currentNodes[2] = nodes.get((currentId + 2) % nodes.size());
			double earTest = triangleUtils.dMatrixDet(currentNodes[2], currentNodes[1], currentNodes[0]);
			double enclosingTest = triangleUtils.dMatrixDet(currentNodes[0], node, currentNodes[2]);
			if (earTest >= 0 && enclosingTest >= 0 && checkTriangleAgainstNodes(nodes, currentNodes, currentId)) {
				flippingUtils.flipTrianglesFromRing(node, nodes, triangles, currentId);
			} else {
				currentNodes[0] = nodes.get((currentId + 1) % nodes.size());
			}
		}
		triangleUtils.mergeTrianglesIntoOne(nodes, triangles);
	}

	private boolean checkTriangleAgainstNodes(List<Point> nodes, Point[] currentNodes, int currentId) {
		for (int i = 0; i < nodes.size() - 3; i++) {
			int index = (currentId + 3 + i) % nodes.size();
			double circumcircleTest = triangleUtils.hMatrixDet(currentNodes[2], currentNodes[1], currentNodes[0], nodes.get(index));
			if (circumcircleTest > 0) {
				return false;
			}
		}
		return true;
	}

}
