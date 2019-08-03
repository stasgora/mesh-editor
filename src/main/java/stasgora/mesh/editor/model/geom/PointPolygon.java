package stasgora.mesh.editor.model.geom;

import stasgora.mesh.editor.model.geom.polygons.Polygon;

import java.io.Serializable;

public class PointPolygon implements Serializable {
	public Point node;
	public Polygon polygon = new Polygon();

	public PointPolygon(Point node) {
		this.node = node;
	}
}
