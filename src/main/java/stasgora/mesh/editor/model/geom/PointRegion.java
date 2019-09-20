package stasgora.mesh.editor.model.geom;

import stasgora.mesh.editor.model.geom.polygons.Polygon;

import java.io.Serializable;

public class PointRegion implements Serializable {
	public final Point node;
	public final Polygon region = new Polygon();

	public PointRegion(Point node) {
		this.node = node;
	}
}
