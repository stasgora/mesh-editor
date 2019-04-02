package sgora.mesh.editor.model.geom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Triangle implements Serializable {

	public Point[] nodes;
	public transient Triangle[] triangles = new Triangle[3];

	private static final long serialVersionUID = 1L;
	public transient int triangleId;
	public int[] triangleIds = new int[3];

	public Triangle(Point[] nodes) {
		this.nodes = nodes;
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

	private void writeObject(ObjectOutputStream out) throws IOException {
		for (int i = 0; i < 3; i++) {
			triangleIds[i] = triangles[i] != null ? triangles[i].triangleId : -1;
		}
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

}
