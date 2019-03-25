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

	@Override
	public String toString() {
		return Arrays.toString(nodes);
	}

}
