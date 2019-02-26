package sgora.mesh.editor.services.triangulation;

import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Triangle;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class TriangleUtils {

	private static final Logger LOGGER = Logger.getLogger(TriangleUtils.class.getName());

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
