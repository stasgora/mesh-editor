package stasgora.mesh.editor.services.mesh.generation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.stasgora.observetree.SettableObservable;
import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Polygon;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.project.CanvasData;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class TriangleUtils {

	private static final Logger LOGGER = Logger.getLogger(TriangleUtils.class.getName());

	private final SettableObservable<Mesh> mesh;

	@Inject
	TriangleUtils(CanvasData canvasData) {
		this.mesh = canvasData.mesh;
	}

	void mergeTrianglesIntoOne(List<Point> nodes, List<Triangle> triangles) {
		Triangle newTriangle = new Triangle(nodes.toArray(new Point[3]));
		for (int i = 0; i < 3; i++) {
			Triangle triangle = triangles.get(i);
			int triNeighbourId = Arrays.asList(triangle.nodes).indexOf(nodes.get(i));
			bindTrianglesBothWays(newTriangle, i, triangle.triangles[triNeighbourId], triangle);
		}
		triangles.forEach(mesh.get()::removeTriangle);
		mesh.get().addTriangle(newTriangle);
	}

	Triangle findTriangleByLocation(Point location) {
		Triangle current = mesh.get().getTriangle(0);
		Triangle next = null;
		do {
			for (int i = 0; i < 3; i++) {
				next = getCloserTriangle(location, current, i);
				if (next != null) {
					current = next;
					break;
				}
			}
		} while (next != null);
		return current;
	}

	private Triangle getCloserTriangle(Point node, Triangle current, int nodeIndex) {
		double det = dMatrixDet(current.nodes[nodeIndex], node, current.nodes[(nodeIndex + 1) % 3]);
		return det + 1e-5 < 0 ? current.triangles[nodeIndex] : null;
	}

	public List<Polygon> getValidVoronoiRegions() {
		Stream<Triangle> boundingTriangleStream = mesh.get().getTriangles().stream().filter(triangle -> Arrays.stream(triangle.nodes).anyMatch(mesh.get().getBoundingNodes()::contains));
		Set<Point> boundingNodes = getTrianglePointSet(boundingTriangleStream.collect(Collectors.toList()));
		return mesh.get().getNodeRegions().stream().filter(region -> !boundingNodes.contains(region.node)).map(pointRegion -> pointRegion.region).collect(Collectors.toList());
	}

	public Set<Point> getTrianglePointSet(List<Triangle> triangles) {
		return triangles.stream().flatMap(triangle -> Arrays.stream(triangle.nodes)).collect(Collectors.toSet());
	}

	public List<Triangle> getValidTriangles() {
		return mesh.get().getTriangles().stream().filter(this::isTriangleValid).collect(Collectors.toList());
	}

	private boolean isTriangleValid(Triangle triangle) {
		List<Point> boundingNodes = mesh.get().getBoundingNodes();
		return Arrays.stream(triangle.nodes).noneMatch(boundingNodes::contains);
	}

	Point getSeparateNode(Triangle from, Triangle with) {
		return from.nodes[getSeparateNodeIndex(from, with)];
	}

	int getSeparateNodeIndex(Triangle from, Triangle with) {
		List<Point> nodes = Arrays.asList(with.nodes);
		for (int i = 0; i < from.nodes.length; i++) {
			if (!nodes.contains(from.nodes[i])) {
				return i;
			}
		}
		LOGGER.warning("No separate node found");
		return -1;
	}

	void bindTrianglesBothWays(Triangle a, int aIndex, Triangle b, Triangle bIndex) {
		a.triangles[aIndex] = b;
		if (b == null) {
			return;
		}
		int index = Arrays.asList(b.triangles).indexOf(bIndex);
		if (index == -1) {
			LOGGER.warning(() -> String.format("Triangle %s is not an neighbour of %s", bIndex, b));
			return;
		}
		b.triangles[index] = a;
	}

	double dMatrixDet(Point a, Point b, Point c) {
		return a.x * b.y + a.y * c.x + b.x * c.y - a.y * b.x - b.y * c.x - c.y * a.x;
	}

	double hMatrixDet(Point a, Point b, Point c, Point d) {
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
