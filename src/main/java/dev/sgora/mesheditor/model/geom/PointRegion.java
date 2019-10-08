package dev.sgora.mesheditor.model.geom;

import dev.sgora.mesheditor.model.geom.polygons.Polygon;

import java.io.Serializable;

public class PointRegion implements Serializable {
	public final Point node;
	public final Polygon region = new Polygon();

	public PointRegion(Point node) {
		this.node = node;
	}
}
