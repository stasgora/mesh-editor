package stasgora.mesh.editor.ui.canvas;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.model.project.MeshType;
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

	public void draw(Point[] nodes, List<Point[]> triangles, List<Point[]> voronoiRegions, Rectangle boundingBox) {
		if(!isVisible()) {
			return;
		}
		if(visualProperties.triangulationVisible.get() && visualProperties.meshType.get() == MeshType.TRIANGULATION)
			drawPolygons(triangles, visualProperties.meshTransparency.get());
		if(visualProperties.meshType.get() == MeshType.VORONOI_DIAGRAM)
			drawPolygons(voronoiRegions, visualProperties.meshTransparency.get());
		if(visualProperties.edgesVisible.get())
			drawEdges(triangles);
		if(visualProperties.nodesVisible.get())
			drawNodes(nodes);
		drawBoundingBox(boundingBox);
	}

	private void drawEdges(List<Point[]> triangles) {
		context.setLineCap(StrokeLineCap.ROUND);
		context.setLineWidth(visualProperties.edgeThickness.get());
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

	private void drawNodes(Point[] nodes) {
		double transparency = visualProperties.meshTransparency.get();
		double nodeRadius = visualProperties.nodeRadius.get();
		for (Point node : nodes) {
			context.setFill(colorUtils.getNodeColor(node).setAlpha(transparency).toFXColor());
			context.fillOval(node.x - nodeRadius / 2d, node.y - nodeRadius / 2d, nodeRadius, nodeRadius);
		}
	}

	private void drawBoundingBox(Rectangle boundingBox) {
		context.setStroke(Color.gray(0.8));
		context.setLineDashes(10, 15);
		context.strokeRect(boundingBox.position.x, boundingBox.position.y, boundingBox.size.x, boundingBox.size.y);
		context.setLineDashes(0);
	}

	private void drawPolygons(List<Point[]> polygons, double transparency) {
		polygons.forEach(polygon -> drawPolygon(polygon, transparency));
	}

	private void drawPolygon(Point[] vertices, double transparency) {
		if(vertices.length < 3)
			return;
		SerializableColor color = colorUtils.getPolygonColor(vertices).setAlpha(transparency);
		context.setFill(color.toFXColor());
		context.beginPath();
		context.moveTo(vertices[0].x, vertices[0].y);
		for (int i = 1; i < vertices.length; i++) {
			context.lineTo(vertices[i].x, vertices[i].y);
		}
		context.closePath();
		context.fill();
	}

}
