package stasgora.mesh.editor.model.geom.polygons;

import stasgora.mesh.editor.model.geom.Point;

import java.io.Serializable;
import java.util.Arrays;

public class Polygon implements Serializable {
	public Point[] nodes;

	protected static final long serialVersionUID = 1L;

	public Polygon(Point[] nodes) {
		this.nodes = nodes;
	}

	public int[] xCoords() {
		return Arrays.stream(nodes).map(point -> (int) Math.round(point.x)).mapToInt(Integer::intValue).toArray();
	}

	public int[] yCoords() {
		return Arrays.stream(nodes).map(point -> (int) Math.round(point.y)).mapToInt(Integer::intValue).toArray();
	}

	@Override
	public String toString() {
		return Arrays.toString(nodes);
	}
}
