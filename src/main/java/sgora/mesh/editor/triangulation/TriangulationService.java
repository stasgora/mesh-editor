package sgora.mesh.editor.triangulation;

import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.observables.SettableObservable;

public class TriangulationService {

	private SettableObservable<Mesh> mesh;

	public TriangulationService(SettableObservable<Mesh> mesh) {
		this.mesh = mesh;
	}

	private Triangle walkToContainerTriangle(Point point) {
		Triangle current = mesh.get().getTriangles().get(0);
		Triangle next = null;
		do {
			for (int i = 0; i < 3; i++) {
				next = getCloserTriangle(point, current, i);
				if(next != null) {
					current = next;
					break;
				}
			}
		} while (next != null);
		return current;
	}

	private Triangle getCloserTriangle(Point point, Triangle current, int nodeIndex) {
		double det = getPointEdgeOrientation(current.nodes[nodeIndex], current.nodes[(nodeIndex + 1) % 3], point);
		return det < 0 ? current.triangles[nodeIndex] : null;
	}

	private double getPointEdgeOrientation(Point point1, Point point2, Point point3) {
		return point1.x * point2.y + point1.y * point3.x + point2.x * point3.y - point1.y * point2.x - point2.y * point3.x - point3.y * point1.x;
	}

	private boolean isPointInsideCircumcircle(Point point, Triangle triangle) {
		double part = triangle.nodes[0].x * (point.y - triangle.nodes[2].y) + triangle.nodes[0].y *
				(triangle.nodes[2].x - point.x) - triangle.nodes[2].x * point.y + triangle.nodes[2].y * point.x;
		return triangle.nodes[1].x * (triangle.nodes[0].x * triangle.nodes[0].x * (triangle.nodes[2].y - point.y) + triangle.nodes[1].x * part
				+ triangle.nodes[0].y * (triangle.nodes[0].y * (triangle.nodes[2].y - point.y) - triangle.nodes[2].x * triangle.nodes[2].x
				- triangle.nodes[2].y * triangle.nodes[2].y + point.x * point.x + point.y * point.y) + triangle.nodes[2].x * triangle.nodes[2].x
				* point.y - triangle.nodes[2].y * point.x * point.x + triangle.nodes[2].y * (triangle.nodes[2].y * point.y - point.y * point.y))
				+ triangle.nodes[1].y * (triangle.nodes[1].y * part + triangle.nodes[0].x * (triangle.nodes[0].x * (point.x - triangle.nodes[2].x)
				+ triangle.nodes[2].x * triangle.nodes[2].x + triangle.nodes[2].y * triangle.nodes[2].y - point.x * point.x - point.y * point.y)
				+ triangle.nodes[0].y * triangle.nodes[0].y * (point.x - triangle.nodes[2].x) + point.x * (-triangle.nodes[2].x * triangle.nodes[2].x
				+ triangle.nodes[2].x * point.x - triangle.nodes[2].y * triangle.nodes[2].y) + triangle.nodes[2].x * point.y * point.y) + triangle.nodes[0].x
				* (triangle.nodes[0].x * (triangle.nodes[2].x * point.y - triangle.nodes[2].y * point.x) + triangle.nodes[2].x * triangle.nodes[2].x
				* (-point.y) + triangle.nodes[2].y * point.x * point.x + triangle.nodes[2].y * (point.y * point.y - triangle.nodes[2].y * point.y))
				+ triangle.nodes[0].y * (triangle.nodes[0].y * (triangle.nodes[2].x * point.y - triangle.nodes[2].y * point.x) + point.x * (triangle.nodes[2].x
				* triangle.nodes[2].x - triangle.nodes[2].x * point.x + triangle.nodes[2].y * triangle.nodes[2].y) - triangle.nodes[2].x * point.y * point.y) > 0;
	}

}
