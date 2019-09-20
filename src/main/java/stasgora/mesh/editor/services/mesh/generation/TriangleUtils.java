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
			int triNeighbourId = Arrays.asList(triangle.getNodes()).indexOf(nodes.get(i));
			bindTrianglesBothWays(newTriangle, i, triangle.getTriangles()[triNeighbourId], triangle);
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
		double det = dMatrixDet(current.getNodes()[nodeIndex], node, current.getNodes()[(nodeIndex + 1) % 3]);
		return det + 1e-5 < 0 ? current.getTriangles()[nodeIndex] : null;
	}

	public List<Polygon> getValidVoronoiRegions() {
		Stream<Triangle> boundingTriangleStream = mesh.get().getTriangles().stream().filter(triangle -> Arrays.stream(triangle.getNodes()).anyMatch(mesh.get().getBoundingNodes()::contains));
		Set<Point> boundingNodes = getTrianglePointSet(boundingTriangleStream.collect(Collectors.toList()));
		return mesh.get().getNodeRegions().stream().filter(region -> !boundingNodes.contains(region.node)).map(pointRegion -> pointRegion.region).collect(Collectors.toList());
	}

	public Set<Point> getTrianglePointSet(List<Triangle> triangles) {
		return triangles.stream().flatMap(triangle -> Arrays.stream(triangle.getNodes())).collect(Collectors.toSet());
	}

	public List<Triangle> getValidTriangles() {
		return mesh.get().getTriangles().stream().filter(this::isTriangleValid).collect(Collectors.toList());
	}

	private boolean isTriangleValid(Triangle triangle) {
		List<Point> boundingNodes = mesh.get().getBoundingNodes();
		return Arrays.stream(triangle.getNodes()).noneMatch(boundingNodes::contains);
	}

	Point getSeparateNode(Triangle from, Triangle with) {
		return from.getNodes()[getSeparateNodeIndex(from, with)];
	}

	int getSeparateNodeIndex(Triangle from, Triangle with) {
		List<Point> nodes = Arrays.asList(with.getNodes());
		for (int i = 0; i < from.getNodes().length; i++) {
			if (!nodes.contains(from.getNodes()[i])) {
				return i;
			}
		}
		LOGGER.warning("No separate node found");
		return -1;
	}

	void bindTrianglesBothWays(Triangle a, int aIndex, Triangle b, Triangle bIndex) {
		a.getTriangles()[aIndex] = b;
		if (b == null) {
			return;
		}
		int index = Arrays.asList(b.getTriangles()).indexOf(bIndex);
		if (index == -1) {
			LOGGER.warning(() -> String.format("Triangle %s is not an neighbour of %s", bIndex, b));
			return;
		}
		b.getTriangles()[index] = a;
	}

	double dMatrixDet(Point a, Point b, Point c) {
		return a.getX() * b.getY() + a.getY() * c.getX() + b.getX() * c.getY() - a.getY() * b.getX() - b.getY() * c.getX() - c.getY() * a.getX();
	}

	double hMatrixDet(Point a, Point b, Point c, Point d) {
		return a.getX() * a.getX() * b.getX() * c.getY() - a.getX() * a.getX() * b.getX() * d.getY() + a.getX() * a.getX() * (-b.getY()) * c.getX() + a.getX() * a.getX() * b.getY() * d.getX() + a.getX() * a.getX() * c.getX() * d.getY() - a.getX() * a.getX() * c.getY() * d.getX()
				- a.getX() * b.getX() * b.getX() * c.getY() + a.getX() * b.getX() * b.getX() * d.getY() - a.getX() * b.getY() * b.getY() * c.getY() + a.getX() * b.getY() * b.getY() * d.getY() + a.getX() * b.getY() * c.getX() * c.getX() + a.getX() * b.getY() * c.getY() * c.getY()
				- a.getX() * b.getY() * d.getX() * d.getX() - a.getX() * b.getY() * d.getY() * d.getY() - a.getX() * c.getX() * c.getX() * d.getY() - a.getX() * c.getY() * c.getY() * d.getY() + a.getX() * c.getY() * d.getX() * d.getX() + a.getX() * c.getY() * d.getY() * d.getY()
				+ a.getY() * a.getY() * b.getX() * c.getY() - a.getY() * a.getY() * b.getX() * d.getY() - a.getY() * a.getY() * b.getY() * c.getX() + a.getY() * a.getY() * b.getY() * d.getX() + a.getY() * a.getY() * c.getX() * d.getY() - a.getY() * a.getY() * c.getY() * d.getX()
				+ a.getY() * b.getX() * b.getX() * c.getX() - a.getY() * b.getX() * b.getX() * d.getX() - a.getY() * b.getX() * c.getX() * c.getX() - a.getY() * b.getX() * c.getY() * c.getY() + a.getY() * b.getX() * d.getX() * d.getX() + a.getY() * b.getX() * d.getY() * d.getY()
				+ a.getY() * b.getY() * b.getY() * c.getX() - a.getY() * b.getY() * b.getY() * d.getX() + a.getY() * c.getX() * c.getX() * d.getX() - a.getY() * c.getX() * d.getX() * d.getX() - a.getY() * c.getX() * d.getY() * d.getY() + a.getY() * c.getY() * c.getY() * d.getX()
				- b.getX() * b.getX() * c.getX() * d.getY() + b.getX() * b.getX() * c.getY() * d.getX() + b.getX() * c.getX() * c.getX() * d.getY() + b.getX() * c.getY() * c.getY() * d.getY() - b.getX() * c.getY() * d.getX() * d.getX() - b.getX() * c.getY() * d.getY() * d.getY()
				- b.getY() * b.getY() * c.getX() * d.getY() + b.getY() * b.getY() * c.getY() * d.getX() - b.getY() * c.getX() * c.getX() * d.getX() + b.getY() * c.getX() * d.getX() * d.getX() + b.getY() * c.getX() * d.getY() * d.getY() - b.getY() * c.getY() * c.getY() * d.getX();
	}

}
