package dev.sgora.mesheditor.services.mesh.rendering;

import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.model.paint.SerializableColor;
import dev.sgora.mesheditor.model.project.VisualProperties;
import dev.sgora.mesheditor.services.drawing.ColorUtils;
import dev.sgora.mesheditor.model.geom.polygons.Polygon;
import dev.sgora.mesheditor.model.geom.polygons.Triangle;
import dev.sgora.mesheditor.model.project.MeshLayer;
import dev.sgora.mesheditor.services.mesh.generation.TriangleUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class MeshRenderer {
	private final ColorUtils colorUtils;
	private final TriangleUtils triangleUtils;
	private VisualProperties visualProperties;

	protected MeshRenderer(ColorUtils colorUtils, TriangleUtils triangleUtils, VisualProperties visualProperties) {
		this.colorUtils = colorUtils;
		this.triangleUtils = triangleUtils;
		this.visualProperties = visualProperties;
	}

	public void render() {
		if (!visualProperties.meshVisible.get())
			return;
		prepareRendering();

		List<Polygon> regions = triangleUtils.getValidVoronoiRegions();
		List<Triangle> triangles = triangleUtils.getValidTriangles();
		MeshLayer voronoiDiagramLayer = visualProperties.voronoiDiagramLayer.get();
		MeshLayer triangulationLayer = visualProperties.triangulationLayer.get();

		if (voronoiDiagramLayer.layerVisible.get())
			drawPolygons(regions, voronoiDiagramLayer);
		if (triangulationLayer.layerVisible.get())
			drawPolygons(triangles, triangulationLayer);
		if (voronoiDiagramLayer.layerVisible.get())
			drawEdges(regions, voronoiDiagramLayer);
		if (triangulationLayer.layerVisible.get())
			drawEdges(triangles, triangulationLayer);
		if (voronoiDiagramLayer.layerVisible.get())
			drawNodes(regions, voronoiDiagramLayer);
		if (triangulationLayer.layerVisible.get())
			drawNodes(triangles, triangulationLayer);
	}

	protected abstract void drawEdge(Point from, Point to, SerializableColor color);

	protected abstract void drawPoint(Point point, double radius, SerializableColor color);

	protected abstract void drawPolygon(Polygon polygon, SerializableColor color);

	protected abstract void setUpEdgeDrawing(double thickness);

	protected void prepareRendering() {
	}

	private void drawNodes(List<? extends Polygon> polygons, MeshLayer layer) {
		if (!layer.nodesVisible.get())
			return;
		List<Point> drawnPoints = new ArrayList<>();
		double transparency = visualProperties.meshTransparency.get() * layer.layerTransparency.get();
		for (Polygon polygon : polygons) {
			for (Point vertex : polygon.getNodes()) {
				if (drawnPoints.contains(vertex))
					continue;
				drawPoint(vertex, layer.nodeRadius.get(), colorUtils.getNodeColor(vertex).setAlpha(transparency));
				drawnPoints.add(vertex);
			}
		}
	}

	private void drawPolygons(List<? extends Polygon> polygons, MeshLayer layer) {
		if (!layer.polygonsVisible.get())
			return;
		double transparency = visualProperties.meshTransparency.get() * layer.layerTransparency.get();
		polygons.forEach(polygon -> drawPolygon(polygon, colorUtils.getPolygonColor(polygon.getNodes()).setAlpha(transparency)));
	}

	private void drawEdges(List<? extends Polygon> polygons, MeshLayer layer) {
		if (!layer.edgesVisible.get())
			return;
		setUpEdgeDrawing(layer.edgeThickness.get());
		double transparency = visualProperties.meshTransparency.get() * layer.layerTransparency.get();
		for (Polygon polygon : polygons) { // FIXME drawing twice most lines
			Point[] nodes = polygon.getNodes();
			for (int i = 0; i < nodes.length; i++) {
				int nextIndex = (i + 1) % nodes.length;
				drawEdge(nodes[i], nodes[nextIndex], colorUtils.getEdgeColor(nodes[i], nodes[nextIndex]).setAlpha(transparency));
			}
		}
	}
}
