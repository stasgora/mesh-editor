package stasgora.mesh.editor.ui.canvas;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.Rectangle;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.drawing.ColorUtils;

import java.util.List;

public class MeshCanvas extends Canvas {

	private ColorUtils colorUtils;
	private VisualProperties visualProperties;

	public void init(ColorUtils colorUtils, VisualProperties visualProperties) {
		this.colorUtils = colorUtils;
		this.visualProperties = visualProperties;
	}

	public void draw(Point[] nodes, List<Point[]> triangles, Rectangle boundingBox) {
		if(!isVisible()) {
			return;
		}
		drawTriangles(triangles);
		drawEdges(triangles);
		drawNodes(nodes);
		drawBoundingBox(boundingBox);
	}

	private void drawEdges(List<Point[]> triangles) {
		if(visualProperties.edgesVisible.get()) {
			context.setLineCap(StrokeLineCap.ROUND);
			context.setLineWidth(visualProperties.lineWidth.get());
			double transparency = visualProperties.meshTransparency.get();
			for (Point[] triangle : triangles) {
				for (int i = 0; i < 3; i++) {
					context.setStroke(colorUtils.getEdgeColor(triangle[i], triangle[(i + 1) % 3]).setAlpha(transparency).toFXColor());
					context.beginPath();
					context.moveTo(triangle[i].x, triangle[i].y);
					context.lineTo(triangle[(i + 1) % 3].x, triangle[(i + 1) % 3].y);
					context.closePath();
					context.stroke();
				}
			}
		}
	}

	private void drawNodes(Point[] nodes) {
		if(visualProperties.nodesVisible.get()) {
			double transparency = visualProperties.meshTransparency.get();
			int nodeRadius = visualProperties.nodeRadius.get();
			for (Point node : nodes) {
				context.setFill(colorUtils.getNodeColor(node).setAlpha(transparency).toFXColor());
				context.fillOval(node.x - nodeRadius / 2d, node.y - nodeRadius / 2d, nodeRadius, nodeRadius);
			}
		}
	}

	private void drawBoundingBox(Rectangle boundingBox) {
		context.setStroke(Color.gray(0.8));
		context.setLineDashes(10, 15);
		context.strokeRect(boundingBox.position.x, boundingBox.position.y, boundingBox.size.x, boundingBox.size.y);
		context.setLineDashes(0);
	}

	private void drawTriangles(List<Point[]> triangles) {
		if(visualProperties.trianglesVisible.get()) {
			double transparency = visualProperties.meshTransparency.get();
			for (Point[] triangle : triangles) {
				drawTriangle(triangle, colorUtils.getTriangleColor(triangle).setAlpha(transparency));
			}
		}
	}

	private void drawTriangle(Point[] triangle, SerializableColor color) {
		context.setFill(color.toFXColor());
		context.beginPath();
		context.moveTo(triangle[0].x, triangle[0].y);
		context.lineTo(triangle[1].x, triangle[1].y);
		context.lineTo(triangle[2].x, triangle[2].y);
		context.closePath();
		context.fill();
	}

}
