package stasgora.mesh.editor.services.mesh.rendering;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Polygon;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.drawing.ColorUtils;
import stasgora.mesh.editor.services.mesh.triangulation.NodeUtils;
import stasgora.mesh.editor.services.mesh.triangulation.TriangleUtils;

import java.awt.*;

public class SvgMeshRenderer extends MeshRenderer {
	private final NodeUtils nodeUtils;
	private SVGGraphics2D graphics;

	public SvgMeshRenderer(TriangleUtils triangleUtils, NodeUtils nodeUtils, ColorUtils colorUtils, VisualProperties visualProperties) {
		super(colorUtils, triangleUtils, visualProperties);
		this.nodeUtils = nodeUtils;
	}

	public String renderAsString() {
		render();
		return graphics.getSVGDocument();
	}

	@Override
	protected void prepareRendering() {
		Rectangle boundingBox = nodeUtils.getProportionalNodeBoundingBox();
		graphics = new SVGGraphics2D((int) boundingBox.size.x, (int) boundingBox.size.y);
		graphics.setBackground(new Color(1f,1f,1f,0f ));
		Point marginSize = nodeUtils.getProportionalMarginSize();
		graphics.translate(marginSize.x, marginSize.y);
	}

	@Override
	protected void drawEdge(Point from, Point to, SerializableColor color) {
		graphics.setColor(color.toAwtColor());
		graphics.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
	}

	@Override
	protected void drawPoint(Point point, double radius, SerializableColor color) {
		graphics.setColor(color.toAwtColor());
		graphics.fillOval((int) (point.x - radius / 2), (int) (point.y - radius / 2), (int) radius, (int) radius);
	}

	@Override
	protected void drawPolygon(Polygon polygon, SerializableColor color) {
		graphics.setColor(color.toAwtColor());
		graphics.fillPolygon(polygon.xCoords(), polygon.yCoords(), polygon.nodes.length);
	}

	@Override
	protected void setUpEdgeDrawing(double thickness) {
		graphics.setStroke(new BasicStroke((float) thickness));
	}
}
