package stasgora.mesh.editor.services.mesh.rendering;

import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Polygon;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.model.project.MeshLayer;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.drawing.ColorUtils;
import stasgora.mesh.editor.services.mesh.triangulation.TriangleUtils;

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
		if(!visualProperties.meshVisible.get())
			return;
		prepareRendering();

		List<Polygon> regions = triangleUtils.getValidVoronoiRegions();
		List<Triangle> triangles = triangleUtils.getValidTriangles();
		MeshLayer voronoiDiagramLayer = visualProperties.voronoiDiagramLayer.get();
		MeshLayer triangulationLayer = visualProperties.triangulationLayer.get();

		if(voronoiDiagramLayer.layerVisible.get())
			drawPolygons(regions, voronoiDiagramLayer);
		if(triangulationLayer.layerVisible.get())
			drawPolygons(triangles, triangulationLayer);
		if(voronoiDiagramLayer.layerVisible.get())
			drawEdges(regions, voronoiDiagramLayer);
		if(triangulationLayer.layerVisible.get())
			drawEdges(triangles, triangulationLayer);
		if(voronoiDiagramLayer.layerVisible.get())
			drawNodes(regions, voronoiDiagramLayer);
		if(triangulationLayer.layerVisible.get())
			drawNodes(triangles, triangulationLayer);
	}

	protected abstract void drawEdge(Point from, Point to, SerializableColor color);
	protected abstract void drawPoint(Point point, double radius, SerializableColor color);
	protected abstract void drawPolygon(Polygon polygon, SerializableColor color);

	protected abstract void setUpEdgeDrawing(double thickness);
	protected void prepareRendering() {}

	private void drawNodes(List<? extends Polygon> polygons, MeshLayer layer) {
		if(!layer.nodesVisible.get())
			return;
		List<Point> drawnPoints = new ArrayList<>();
		double transparency = visualProperties.meshTransparency.get() * layer.layerTransparency.get();
		for (Polygon polygon : polygons) {
			for (Point vertex : polygon.nodes) {
				if(drawnPoints.contains(vertex))
					continue;
				drawPoint(vertex, layer.nodeRadius.get(), colorUtils.getNodeColor(vertex).setAlpha(transparency));
				drawnPoints.add(vertex);
			}
		}
	}

	private void drawPolygons(List<? extends Polygon> polygons, MeshLayer layer) {
		if(!layer.polygonsVisible.get())
			return;
		double transparency = visualProperties.meshTransparency.get() * layer.layerTransparency.get();
		polygons.forEach(polygon -> drawPolygon(polygon, colorUtils.getPolygonColor(polygon.nodes).setAlpha(transparency)));
	}

	private void drawEdges(List<? extends Polygon> polygons, MeshLayer layer) {
		if(!layer.edgesVisible.get())
			return;
		setUpEdgeDrawing(layer.edgeThickness.get());
		double transparency = visualProperties.meshTransparency.get() * layer.layerTransparency.get();
		for (Polygon polygon : polygons) { // FIXME drawing twice most lines
			Point[] nodes = polygon.nodes;
			for (int i = 0; i < nodes.length; i++) {
				int nextIndex = (i + 1) % nodes.length;
				drawEdge(nodes[i], nodes[nextIndex], colorUtils.getEdgeColor(nodes[i], nodes[nextIndex]).setAlpha(transparency));
			}
		}
	}
}
