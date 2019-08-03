package stasgora.mesh.editor.services.mesh.triangulation;

import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import io.github.stasgora.observetree.SettableObservable;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TriangleUtils {

	private static final Logger LOGGER = Logger.getLogger(TriangleUtils.class.getName());

	private SettableObservable<Mesh> mesh;
	private NodeUtils nodeUtils;

	public TriangleUtils(SettableObservable<Mesh> mesh, NodeUtils nodeUtils) {
		this.mesh = mesh;
		this.nodeUtils = nodeUtils;
	}

	void mergeTrianglesIntoOne(List<Point> nodes, List<Triangle> triangles) {
		Mesh mesh = this.mesh.get();
		Triangle newTriangle = new Triangle(nodes.toArray(new Point[3]));
		for (int i = 0; i < 3; i++) {
			Triangle triangle = triangles.get(i);
			int triNeighbourId = Arrays.asList(triangle.nodes).indexOf(nodes.get(i));
			bindTrianglesBothWays(newTriangle, i, triangle.triangles[triNeighbourId], triangle);
		}
		triangles.forEach(mesh::removeTriangle);
		mesh.addTriangle(newTriangle);
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
		double det = D_matrixDet(current.nodes[nodeIndex], node, current.nodes[(nodeIndex + 1) % 3]);
		return det + 1e-5 < 0 ? current.triangles[nodeIndex] : null;
	}

	public List<Point[]> getCanvasSpaceTriangles() {
		return getValidTriangles().stream().map(this::getCanvasSpaceTriangle).collect(Collectors.toList());
	}

	public List<Triangle> getValidTriangles() {
		return mesh.get().getTriangles().stream().filter(this::isTriangleValid).collect(Collectors.toList());
	}

	private boolean isTriangleValid(Triangle triangle) {
		List<Point> boundingNodes = mesh.get().boundingNodes;
		for (Point node : triangle.nodes) {
			if (boundingNodes.contains(node)) {
				return false;
			}
		}
		return true;
	}

	public Point[] getCanvasSpaceTriangle(Triangle triangle) {
		return Arrays.stream(triangle.nodes).map(node -> nodeUtils.proportionalToCanvasPos(new Point(node))).toArray(Point[]::new);
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
		if(b == null) {
			return;
		}
		int index = Arrays.asList(b.triangles).indexOf(bIndex);
		if(index == -1) {
			LOGGER.warning("Triangle " + bIndex + " is not an neighbour of " + b);
			return;
		}
		b.triangles[index] = a;
	}

	double D_matrixDet(Point a, Point b, Point c) {
		return a.x * b.y + a.y * c.x + b.x * c.y - a.y * b.x - b.y * c.x - c.y * a.x;
	}

	double H_matrixDet(Point a, Point b, Point c, Point d) {
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
