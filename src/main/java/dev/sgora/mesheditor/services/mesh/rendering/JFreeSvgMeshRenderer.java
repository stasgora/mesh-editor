package dev.sgora.mesheditor.services.mesh.rendering;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.model.paint.SerializableColor;
import dev.sgora.mesheditor.model.project.VisualProperties;
import dev.sgora.mesheditor.services.drawing.ColorUtils;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import dev.sgora.mesheditor.model.geom.polygons.Polygon;
import dev.sgora.mesheditor.model.geom.polygons.Rectangle;
import dev.sgora.mesheditor.services.mesh.generation.NodeUtils;
import dev.sgora.mesheditor.services.mesh.generation.TriangleUtils;

import java.awt.*;

@Singleton
class JFreeSvgMeshRenderer extends MeshRenderer implements SvgMeshRenderer {
	private final NodeUtils nodeUtils;
	private SVGGraphics2D graphics;

	@Inject
	JFreeSvgMeshRenderer(TriangleUtils triangleUtils, NodeUtils nodeUtils, ColorUtils colorUtils, VisualProperties visualProperties) {
		super(colorUtils, triangleUtils, visualProperties);
		this.nodeUtils = nodeUtils;
	}

	public String renderSvg() {
		render();
		return graphics.getSVGDocument();
	}

	@Override
	protected void prepareRendering() {
		Rectangle boundingBox = nodeUtils.getProportionalNodeBoundingBox();
		graphics = new SVGGraphics2D((int) boundingBox.getSize().getX(), (int) boundingBox.getSize().getY());
		graphics.setBackground(new Color(1f, 1f, 1f, 0f));
		dev.sgora.mesheditor.model.geom.Point marginSize = nodeUtils.getProportionalMarginSize();
		graphics.translate(marginSize.getX(), marginSize.getY());
	}

	@Override
	protected void drawEdge(dev.sgora.mesheditor.model.geom.Point from, dev.sgora.mesheditor.model.geom.Point to, SerializableColor color) {
		graphics.setColor(color.toAwtColor());
		graphics.drawLine((int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());
	}

	@Override
	protected void drawPoint(Point point, double radius, SerializableColor color) {
		graphics.setColor(color.toAwtColor());
		graphics.fillOval((int) (point.getX() - radius / 2), (int) (point.getY() - radius / 2), (int) radius, (int) radius);
	}

	@Override
	protected void drawPolygon(Polygon polygon, SerializableColor color) {
		graphics.setColor(color.toAwtColor());
		graphics.fillPolygon(polygon.xCoords(), polygon.yCoords(), polygon.getNodes().length);
	}

	@Override
	protected void setUpEdgeDrawing(double thickness) {
		graphics.setStroke(new BasicStroke((float) thickness));
	}
}
