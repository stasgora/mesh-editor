package stasgora.mesh.editor.model.geom.polygons;

import stasgora.mesh.editor.model.geom.Point;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class Triangle extends Polygon {
	private static final Logger LOGGER = Logger.getLogger(Triangle.class.getName());

	public transient Triangle[] triangles = new Triangle[3];

	public transient int triangleId;
	public int[] triangleIds = new int[3];

	public Triangle(Point[] nodes) {
		super(nodes);
		if(nodes.length != 3)
			LOGGER.warning("Triangle initialized with " + nodes.length + " points");
	}

	public Triangle(Point a, Point b, Point c) {
		super(new Point[]{a, b, c});
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
