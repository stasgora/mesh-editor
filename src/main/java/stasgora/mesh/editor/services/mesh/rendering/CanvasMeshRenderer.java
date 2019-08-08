package stasgora.mesh.editor.services.mesh.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Polygon;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.drawing.ColorUtils;
import stasgora.mesh.editor.services.mesh.triangulation.NodeUtils;
import stasgora.mesh.editor.services.mesh.triangulation.TriangleUtils;

import java.util.Arrays;

public class CanvasMeshRenderer extends MeshRenderer {
	private GraphicsContext context;
	private final NodeUtils nodeUtils;

	public CanvasMeshRenderer(TriangleUtils triangleUtils, NodeUtils nodeUtils, ColorUtils colorUtils, VisualProperties visualProperties) {
		super(colorUtils, triangleUtils, visualProperties);
		this.nodeUtils = nodeUtils;
	}

	public void setContext(GraphicsContext context) {
		this.context = context;
	}

	@Override
	protected void drawEdge(Point from, Point to, SerializableColor color) {
		context.setStroke(color.toFXColor());
		createPath(new Point[] {from, to});
		context.stroke();
	}

	@Override
	protected void drawPoint(Point point, double radius, SerializableColor color) {
		context.setFill(color.toFXColor());
		point = nodeUtils.proportionalToCanvasPos(point);
		context.fillOval(point.x - radius / 2d, point.y - radius / 2d, radius, radius);
	}

	@Override
	protected void drawPolygon(Polygon polygon, SerializableColor color) {
		context.setFill(color.toFXColor());
		createPath(polygon.nodes);
		context.fill();
	}

	@Override
	protected void setUpEdgeDrawing(double thickness) {
		context.setLineCap(StrokeLineCap.ROUND);
		context.setLineWidth(thickness);
	}

	public void drawBoundingBox(Rectangle boundingBox) {
		context.setStroke(Color.gray(0.8));
		context.setLineDashes(10, 15);
		context.strokeRect(boundingBox.position.x, boundingBox.position.y, boundingBox.size.x, boundingBox.size.y);
		context.setLineDashes(0);
	}

	private void createPath(Point[] vertices) {
		vertices = Arrays.stream(vertices).map(nodeUtils::proportionalToCanvasPos).toArray(Point[]::new);
		context.beginPath();
		context.moveTo(vertices[0].x, vertices[0].y);
		for (int i = 1; i < vertices.length; i++) {
			context.lineTo(vertices[i].x, vertices[i].y);
		}
		context.closePath();
		context.fill();
	}
}
