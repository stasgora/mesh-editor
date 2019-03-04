package sgora.mesh.editor.services.triangulation;

import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.observables.SettableObservable;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class FlippingUtils {

	private final SettableObservable<Mesh> mesh;
	private final TriangleUtils triangleUtils;

	public FlippingUtils(SettableObservable<Mesh> mesh, TriangleUtils triangleUtils) {
		this.mesh = mesh;
		this.triangleUtils = triangleUtils;
	}

	void flipInvalidTriangles(Stack<Triangle> remaining) {
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

	void flipTrianglesFromRing(Point node, List<Point> nodes, List<Triangle> triangles, int currentId) {
		int nextId = (currentId + 1) % nodes.size();
		Triangle[] currentTriangles = new Triangle[] { triangles.get(currentId), triangles.get(nextId) };
		Triangle[] newTriangles = flipTriangles(currentTriangles[0], currentTriangles[1]);
		Triangle newNeighbour = Arrays.asList(newTriangles[0].nodes).contains(node) ? newTriangles[0] : newTriangles[1];
		nodes.remove(nextId);
		triangles.add(currentId, newNeighbour);
		triangles.remove(currentTriangles[0]);
		triangles.remove(currentTriangles[1]);
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

	private boolean isPointInsideCircumcircle(Point node, Triangle triangle) {
		return triangleUtils.H_matrixDet(triangle.nodes[2], triangle.nodes[1], triangle.nodes[0], node) > 0;
	}

}
