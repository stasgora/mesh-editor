package stasgora.mesh.editor.ui.canvas;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.model.project.MeshLayer;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.drawing.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class MeshCanvas extends Canvas {

	private ColorUtils colorUtils;
	private VisualProperties visualProperties;

	public void init(ColorUtils colorUtils, VisualProperties visualProperties) {
		this.colorUtils = colorUtils;
		this.visualProperties = visualProperties;
	}

	public void draw(List<Point[]> triangles, List<Point[]> regions, Rectangle boundingBox) {
		if(!isVisible()) {
			return;
		}
		drawLayer(visualProperties.triangulationLayer.get(), triangles);
		drawLayer(visualProperties.voronoiDiagramLayer.get(), regions);
		drawBoundingBox(boundingBox);
	}

	private void drawLayer(MeshLayer layer, List<Point[]> polygons) {
		if(!layer.layerVisible.get())
			return;
		if(layer.polygonsVisible.get())
			drawPolygons(polygons, visualProperties.meshTransparency.get());
		if(layer.edgesVisible.get())
			drawEdges(polygons, layer);
		if(layer.nodesVisible.get())
			drawNodes(polygons, layer);
	}

	private void drawEdges(List<Point[]> polygons, MeshLayer layer) {
		context.setLineCap(StrokeLineCap.ROUND);
		context.setLineWidth(layer.edgeThickness.get());
		double transparency = layer.layerTransparency.get();
		for (Point[] polygon : polygons) { // FIXME drawing twice most lines
			for (int i = 0; i < polygon.length - 1; i++) {
				int nextIndex = (i + 1) % polygon.length;
				context.setStroke(colorUtils.getEdgeColor(polygon[i], polygon[nextIndex]).setAlpha(transparency).toFXColor());
				context.beginPath();
				context.moveTo(polygon[i].x, polygon[i].y);
				context.lineTo(polygon[nextIndex].x, polygon[nextIndex].y);
				context.closePath();
				context.stroke();
			}
		}
	}

	private void drawNodes(List<Point[]> polygons, MeshLayer layer) {
		double transparency = layer.layerTransparency.get();
		double nodeRadius = layer.nodeRadius.get();
		List<Point> drawnPoints = new ArrayList<>();
		for (Point[] polygon : polygons) {
			for (Point vertex : polygon) {
				if(drawnPoints.contains(vertex))
					continue;
				context.setFill(colorUtils.getNodeColor(vertex).setAlpha(transparency).toFXColor());
				context.fillOval(vertex.x - nodeRadius / 2d, vertex.y - nodeRadius / 2d, nodeRadius, nodeRadius);
				drawnPoints.add(vertex);
			}
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
