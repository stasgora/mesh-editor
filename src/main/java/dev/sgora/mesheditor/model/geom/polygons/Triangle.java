package dev.sgora.mesheditor.model.geom.polygons;

import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.model.geom.Line;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

public class Triangle extends Polygon {
	private static final Logger LOGGER = Logger.getLogger(Triangle.class.getName());

	private transient Triangle[] triangles = new Triangle[3];

	private transient int triangleId;
	public final int[] triangleIds = new int[3];

	public Triangle(Point[] nodes) {
		super(nodes);
		if (nodes.length != 3)
			LOGGER.warning(() -> String.format("Triangle initialized with %d points", nodes.length));
	}

	public Triangle(Point a, Point b, Point c) {
		super(new Point[]{a, b, c});
	}

	public Point circumcenter() {
		return Line.bisectionOf(nodes[0], nodes[1]).intersectWith(Line.bisectionOf(nodes[1], nodes[2]));
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

	public Triangle[] getTriangles() {
		return triangles;
	}

	public void setTriangles(Triangle[] triangles) {
		this.triangles = triangles;
	}

	public int getTriangleId() {
		return triangleId;
	}

	public void setTriangleId(int triangleId) {
		this.triangleId = triangleId;
	}
}
