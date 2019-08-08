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
		prepareRendering();

		List<Polygon> regions = triangleUtils.getValidVoronoiRegions();
		List<Triangle> triangles = triangleUtils.getValidTriangles();
		MeshLayer voronoiDiagramLayer = visualProperties.voronoiDiagramLayer.get();
		MeshLayer triangulationLayer = visualProperties.triangulationLayer.get();
		double baseTransparency = visualProperties.meshTransparency.get();
		double voronoiTransparency = baseTransparency * voronoiDiagramLayer.layerTransparency.get();
		double triangulationTransparency = baseTransparency * triangulationLayer.layerTransparency.get();

		drawPolygons(regions, voronoiTransparency);
		drawPolygons(triangles, triangulationTransparency);
		drawEdges(regions, voronoiDiagramLayer.edgeThickness.get(), voronoiTransparency);
		drawEdges(triangles, triangulationLayer.edgeThickness.get(), triangulationTransparency);
		drawNodes(regions, voronoiDiagramLayer.nodeRadius.get(), voronoiTransparency);
		drawNodes(triangles, triangulationLayer.nodeRadius.get(), triangulationTransparency);
	}

	protected abstract void drawEdge(Point from, Point to, SerializableColor color);
	protected abstract void drawPoint(Point point, double radius, SerializableColor color);
	protected abstract void drawPolygon(Polygon polygon, SerializableColor color);

	protected abstract void setUpEdgeDrawing(double thickness);
	protected void prepareRendering() {}

	private void drawNodes(List<? extends Polygon> polygons, double radius, double transparency) {
		List<Point> drawnPoints = new ArrayList<>();
		for (Polygon polygon : polygons) {
			for (Point vertex : polygon.nodes) {
				if(drawnPoints.contains(vertex))
					continue;
				drawPoint(vertex, radius, colorUtils.getNodeColor(vertex).setAlpha(transparency));
				drawnPoints.add(vertex);
			}
		}
	}

	private void drawPolygons(List<? extends Polygon> polygons, double transparency) {
		polygons.forEach(polygon -> drawPolygon(polygon, colorUtils.getPolygonColor(polygon.nodes).setAlpha(transparency)));
	}

	private void drawEdges(List<? extends Polygon> polygons, double thickness, double transparency) {
		setUpEdgeDrawing(thickness);
		for (Polygon polygon : polygons) { // FIXME drawing twice most lines
			Point[] nodes = polygon.nodes;
			for (int i = 0; i < nodes.length - 1; i++) {
				int nextIndex = (i + 1) % nodes.length;
				drawEdge(nodes[i], nodes[nextIndex], colorUtils.getEdgeColor(nodes[i], nodes[nextIndex]).setAlpha(transparency));
			}
		}
	}
}
