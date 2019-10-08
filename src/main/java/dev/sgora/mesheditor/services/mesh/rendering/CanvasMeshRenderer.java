package dev.sgora.mesheditor.services.mesh.rendering;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.model.paint.SerializableColor;
import dev.sgora.mesheditor.model.project.VisualProperties;
import dev.sgora.mesheditor.services.drawing.ColorUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import dev.sgora.mesheditor.model.geom.polygons.Polygon;
import dev.sgora.mesheditor.model.geom.polygons.Rectangle;
import dev.sgora.mesheditor.services.mesh.generation.NodeUtils;
import dev.sgora.mesheditor.services.mesh.generation.TriangleUtils;

import java.util.Arrays;

@Singleton
class CanvasMeshRenderer extends MeshRenderer implements CanvasRenderer {
	private GraphicsContext context;
	private final NodeUtils nodeUtils;

	@Inject
	CanvasMeshRenderer(TriangleUtils triangleUtils, NodeUtils nodeUtils, ColorUtils colorUtils, VisualProperties visualProperties) {
		super(colorUtils, triangleUtils, visualProperties);
		this.nodeUtils = nodeUtils;
	}

	@Override
	public void setContext(GraphicsContext context) {
		this.context = context;
	}

	@Override
	protected void drawEdge(Point from, Point to, SerializableColor color) {
		context.setStroke(color.toFXColor());
		createPath(new Point[]{from, to});
		context.stroke();
	}

	@Override
	protected void drawPoint(Point point, double radius, SerializableColor color) {
		context.setFill(color.toFXColor());
		radius /= nodeUtils.proportionalScaleFactor();
		point = nodeUtils.proportionalToCanvasPos(point);
		context.fillOval(point.getX() - radius / 2d, point.getY() - radius / 2d, radius, radius);
	}

	@Override
	protected void drawPolygon(Polygon polygon, SerializableColor color) {
		context.setFill(color.toFXColor());
		createPath(polygon.getNodes());
		context.fill();
	}

	@Override
	protected void setUpEdgeDrawing(double thickness) {
		context.setLineCap(StrokeLineCap.ROUND);
		thickness /= nodeUtils.proportionalScaleFactor();
		context.setLineWidth(thickness);
	}

	@Override
	public void drawBoundingBox(Rectangle boundingBox) {
		context.setStroke(Color.gray(0.4));
		context.setLineDashes(10, 15);
		context.strokeRect(boundingBox.getPosition().getX(), boundingBox.getPosition().getY(), boundingBox.getSize().getX(), boundingBox.getSize().getY());
		context.setLineDashes(0);
	}

	private void createPath(Point[] vertices) {
		vertices = Arrays.stream(vertices).map(nodeUtils::proportionalToCanvasPos).toArray(Point[]::new);
		context.beginPath();
		context.moveTo(vertices[0].getX(), vertices[0].getY());
		for (int i = 1; i < vertices.length; i++) {
			context.lineTo(vertices[i].getX(), vertices[i].getY());
		}
		context.closePath();
		context.fill();
	}
}
