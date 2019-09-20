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
public class FlippingUtils {

	private final SettableObservable<Mesh> mesh;
	private final TriangleUtils triangleUtils;

	@Inject
	FlippingUtils(CanvasData canvasData, TriangleUtils triangleUtils) {
		this.mesh = canvasData.mesh;
		this.triangleUtils = triangleUtils;
	}

	List<Triangle> flipInvalidTriangles(Deque<Triangle> remaining) {
		List<Triangle> changedTriangles = new ArrayList<>();
		while (!remaining.isEmpty()) {
			Triangle current = remaining.pop();
			if (!mesh.get().getTriangles().contains(current)) {
				continue;
			}
			for (Triangle neighbour : current.triangles) {
				if (neighbour == null) {
					continue;
				}
				Point otherNode = triangleUtils.getSeparateNode(neighbour, current);
				if (isPointInsideCircumcircle(otherNode, current)) {
					Triangle[] created = flipTriangles(current, neighbour);
					changedTriangles.addAll(Arrays.asList(created));
					remaining.addAll(Arrays.asList(created));
				}
			}
		}
		return changedTriangles;
	}

	void flipTrianglesFromRing(Point node, List<Point> nodes, List<Triangle> triangles, int currentId) {
		int nextId = (currentId + 1) % nodes.size();
		Triangle[] currentTriangles = new Triangle[]{triangles.get(currentId), triangles.get(nextId)};
		Triangle[] newTriangles = flipTriangles(currentTriangles[0], currentTriangles[1]);
		Triangle newNeighbour = Arrays.asList(newTriangles[0].getNodes()).contains(node) ? newTriangles[0] : newTriangles[1];
		nodes.remove(nextId);
		triangles.add(currentId, newNeighbour);
		triangles.remove(currentTriangles[0]);
		triangles.remove(currentTriangles[1]);
	}

	private Triangle[] flipTriangles(Triangle a, Triangle b) {
		int aNodeIndex = triangleUtils.getSeparateNodeIndex(a, b);
		int bNodeIndex = triangleUtils.getSeparateNodeIndex(b, a);
		mesh.get().removeTriangle(a);
		mesh.get().removeTriangle(b);
		Triangle[] added = new Triangle[2];
		added[0] = new Triangle(a.getNodes()[aNodeIndex], a.getNodes()[(aNodeIndex + 1) % 3], b.getNodes()[bNodeIndex]);
		added[1] = new Triangle(b.getNodes()[bNodeIndex], b.getNodes()[(bNodeIndex + 1) % 3], a.getNodes()[aNodeIndex]);
		triangleUtils.bindTrianglesBothWays(added[0], 0, a.triangles[aNodeIndex], a);
		triangleUtils.bindTrianglesBothWays(added[0], 1, b.triangles[(bNodeIndex + 2) % 3], b);
		triangleUtils.bindTrianglesBothWays(added[1], 0, b.triangles[bNodeIndex], b);
		triangleUtils.bindTrianglesBothWays(added[1], 1, a.triangles[(aNodeIndex + 2) % 3], a);
		added[0].triangles[2] = added[1];
		added[1].triangles[2] = added[0];
		mesh.get().addTriangle(added[0]);
		mesh.get().addTriangle(added[1]);
		return added;
	}

	private boolean isPointInsideCircumcircle(Point node, Triangle triangle) {
		return triangleUtils.hMatrixDet(triangle.getNodes()[2], triangle.getNodes()[1], triangle.getNodes()[0], node) > 0;
	}

}
