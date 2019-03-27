package sgora.mesh.editor.services.drawing;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.project.VisualProperties;
import sgora.mesh.editor.services.triangulation.NodeUtils;
import sgora.mesh.editor.services.triangulation.TriangleUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

		List<Point> nodes = canvasData.mesh.get().getNodes();
		List<Triangle> triangles = triangleUtils.getValidTriangles();
		double transparency = visualProperties.meshTransparency.get();
		for (Triangle triangle : triangles) {
			svg.setColor(colorUtils.getTriangleColor(triangleUtils.getCanvasSpaceTriangle(triangle)).setAlpha(transparency).toAwtColor());
			svg.fillPolygon(triangle.xCoords(), triangle.yCoords(), 3);
		}
		return svg.getSVGDocument();
	}

}
