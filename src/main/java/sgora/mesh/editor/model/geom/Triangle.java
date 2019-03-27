package sgora.mesh.editor.model.geom;

import java.io.Serializable;
import java.util.Arrays;

public class Triangle implements Serializable {

	public Point[] nodes;
	public Triangle[] triangles;

	private static final long serialVersionUID = 1L;

	public Triangle(Point[] nodes) {
		this.nodes = nodes;
		triangles = new Triangle[3];
	}

	public Triangle(Point a, Point b, Point c) {
		this(new Point[]{a, b, c});
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
