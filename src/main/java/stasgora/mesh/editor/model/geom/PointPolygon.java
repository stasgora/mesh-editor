package stasgora.mesh.editor.model.geom;

import stasgora.mesh.editor.model.geom.polygons.Polygon;

import java.io.Serializable;

public class PointPolygon implements Serializable {
	public Point node;
	public Polygon polygon;

	public PointPolygon(Point node, Polygon polygon) {
		this.node = node;
		this.polygon = polygon;
	}
}
