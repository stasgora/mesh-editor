package stasgora.mesh.editor.services.files;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.model.project.MeshLayer;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.drawing.ColorUtils;
import stasgora.mesh.editor.services.mesh.triangulation.NodeUtils;
import stasgora.mesh.editor.services.mesh.triangulation.TriangleUtils;

import java.awt.*;
import java.util.List;

public class SvgService {

	private final CanvasData canvasData;
	private final VisualProperties visualProperties;
	private final NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private ColorUtils colorUtils;

	public SvgService(CanvasData canvasData, VisualProperties visualProperties, NodeUtils nodeUtils, TriangleUtils triangleUtils, ColorUtils colorUtils) {
		this.canvasData = canvasData;
		this.visualProperties = visualProperties;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
		this.colorUtils = colorUtils;
	}

	public String createSvg() {
		Rectangle boundingBox = nodeUtils.getProportionalNodeBoundingBox();
		SVGGraphics2D svg = new SVGGraphics2D((int) boundingBox.size.x, (int) boundingBox.size.y);
		svg.setBackground(new Color(1f,1f,1f,0f ));
		Point marginSize = nodeUtils.getProportionalMarginSize();
		svg.translate(marginSize.x, marginSize.y);

		drawLayer(visualProperties.triangulationLayer.get(), svg);
		drawLayer(visualProperties.voronoiDiagramLayer.get(), svg);
		return svg.getSVGDocument();
	}

	private void drawLayer(MeshLayer layer, SVGGraphics2D svg) {
		if(!layer.layerVisible.get())
			return;
		if(layer.polygonsVisible.get())
			drawTriangles(svg, layer.layerTransparency.get());
		if(layer.edgesVisible.get())
			drawEdges(layer, svg);
		if(layer.nodesVisible.get())
			drawNodes(layer, svg);
	}

	private void drawEdges(MeshLayer layer, SVGGraphics2D svg) {
		List<Triangle> triangles = triangleUtils.getValidTriangles();
		double transparency = layer.layerTransparency.get();
		svg.setStroke(new BasicStroke(layer.edgeThickness.get().floatValue()));
		for (int i = 0; i < triangles.size(); i++) {
			Triangle triangle = triangles.get(i);
			for (int j = 0; j < 3; j++) {
				if(triangles.indexOf(triangle.triangles[j]) < i) {
					Point node1 = triangle.nodes[j];
					Point node2 = triangle.nodes[(j + 1) % 3];
					svg.setColor(colorUtils.getEdgeColor(node1, node2).setAlpha(transparency).toAwtColor());
					svg.drawLine((int) node1.x, (int) node1.y, (int) node2.x, (int) node2.y);
				}
			}
		}
	}

	private void drawNodes(MeshLayer layer, SVGGraphics2D svg) {
		List<Point> nodes = canvasData.mesh.get().getNodes();
		double transparency = layer.layerTransparency.get();
		double nodeRadius = layer.nodeRadius.get();
		for (Point node : nodes) {
			svg.setColor(colorUtils.getNodeColor(node).setAlpha(transparency).toAwtColor());
			svg.fillOval((int) (node.x - nodeRadius / 2), (int) (node.y - nodeRadius / 2), (int) nodeRadius, (int) nodeRadius);
		}
	}

	private void drawTriangles(SVGGraphics2D svg, double transparency) {
		List<Triangle> triangles = triangleUtils.getValidTriangles();
		for (Triangle triangle : triangles) {
			//svg.setColor(colorUtils.getPolygonColor(triangleUtils.convertToCanvasSpace(triangle)).setAlpha(transparency).toAwtColor());
			svg.fillPolygon(triangle.xCoords(), triangle.yCoords(), 3);
		}
	}

}
