package dev.sgora.mesheditor.model.geom.polygons;

import dev.sgora.mesheditor.model.geom.Point;

import java.io.Serializable;
import java.util.Arrays;

public class Polygon implements Serializable {
	protected Point[] nodes = new Point[0];

	protected static final long serialVersionUID = 1L;

	public Polygon() {
	}

	public Polygon(Point[] nodes) {
		this.nodes = nodes;
	}

	public int[] xCoords() {
		return Arrays.stream(nodes).map(point -> (int) Math.round(point.getX())).mapToInt(Integer::intValue).toArray();
	}

	public int[] yCoords() {
		return Arrays.stream(nodes).map(point -> (int) Math.round(point.getY())).mapToInt(Integer::intValue).toArray();
	}

	@Override
	public String toString() {
		return Arrays.toString(nodes);
	}

	public Point[] getNodes() {
		return nodes;
	}

	public void setNodes(Point[] nodes) {
		this.nodes = nodes;
	}
}
