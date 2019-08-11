package stasgora.mesh.editor.services.mesh.generation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class NodeUtils {

	private static final Logger LOGGER = Logger.getLogger(NodeUtils.class.getName());

	private final AppConfigReader appConfig;
	private final CanvasData canvasData;

	private final double REL_SPACE_FACTOR;

	@Inject
	NodeUtils(@AppConfig AppConfigReader appConfig, CanvasData canvasData) {
		this.appConfig = appConfig;
		this.canvasData = canvasData;
		REL_SPACE_FACTOR = appConfig.getDouble("meshBox.proportionalSpaceFactor");
	}

	public Triangle findNodeTriangle(Point node) {
		return canvasData.mesh.get().getTriangles().stream().filter(triangle -> Arrays.stream(triangle.nodes).anyMatch(vertex -> vertex == node)).findFirst().orElse(null);
	}

	Point getClosestNode(Point location, Triangle triangle) {
		double nodeBoxRadius = appConfig.getDouble("meshBox.nodeBoxRadius") / (canvasData.imageBox.size.x / REL_SPACE_FACTOR);
		for (Point node : triangle.nodes) {
			Point dist = new Point(node).subtract(location).abs();
			if (dist.x <= nodeBoxRadius && dist.y <= nodeBoxRadius) {
				return node;
			}
		}
		return null;
	}

	public void getNodeNeighbours(Point node, Triangle firstTriangle, List<Point> outPoints, List<Triangle> outTriangles) {
		Triangle currentTriangle = firstTriangle;
		do {
			int nodeIndex = Arrays.asList(currentTriangle.nodes).indexOf(node);
			if (nodeIndex == -1) {
				LOGGER.warning("triangle " + currentTriangle + " does not contain given node " + node);
			}
			nodeIndex = (nodeIndex + 2) % 3;
			if (outPoints != null)
				outPoints.add(currentTriangle.nodes[nodeIndex]);
			currentTriangle = currentTriangle.triangles[nodeIndex];
			if (outTriangles != null)
				outTriangles.add(currentTriangle);
		} while (currentTriangle != firstTriangle);
	}

	public Point proportionalToCanvasPos(Point node) {
		Rectangle imageBox = canvasData.imageBox;
		return new Point(node).multiplyByScalar(imageBox.size.x / REL_SPACE_FACTOR).add(imageBox.position);
	}

	public Point canvasToProportionalPos(Point node) {
		Rectangle imageBox = canvasData.imageBox;
		return new Point(node).subtract(imageBox.position).divideByScalar(imageBox.size.x / REL_SPACE_FACTOR);
	}

	public Point canvasToProportionalSize(Point node) {
		return new Point(node).multiplyByScalar(proportionalScaleFactor());
	}

	public double proportionalScaleFactor() {
		Rectangle imageBox = canvasData.imageBox;
		return REL_SPACE_FACTOR / imageBox.size.x;
	}

	public Point proportionalToPixelPos(Point node) {
		return new Point(node).multiplyByScalar(canvasData.baseImage.get().getWidth() / REL_SPACE_FACTOR);
	}

	public Point getProportionalMarginSize() {
		double spaceAroundImage = appConfig.getDouble("meshBox.spaceAroundImage");
		return canvasToProportionalSize(new Point(canvasData.imageBox.size).multiplyByScalar(spaceAroundImage));
	}

	public Rectangle getCanvasSpaceNodeBoundingBox() {
		Rectangle imageBox = canvasData.imageBox;
		double spaceAroundImage = appConfig.getDouble("meshBox.spaceAroundImage");
		Rectangle area = new Rectangle();
		Point boxSize = imageBox.size;
		area.position = new Point(imageBox.position).subtract(new Point(boxSize).multiplyByScalar(spaceAroundImage));
		area.size = new Point(boxSize).multiplyByScalar(spaceAroundImage * 2 + 1);
		return area;
	}

	Point[] getBoundingNodes() {
		Rectangle boundingBox = getProportionalNodeBoundingBox();
		double majorLength = boundingBox.size.x + boundingBox.size.y;
		return new Point[]{
				new Point(boundingBox.position.x + boundingBox.size.x / 2, -majorLength),
				new Point(boundingBox.position.x - majorLength, boundingBox.size.y),
				new Point(boundingBox.position.x + boundingBox.size.x + majorLength, boundingBox.size.y)
		};
	}

	public Rectangle getProportionalNodeBoundingBox() {
		Rectangle canvasSpaceBox = getCanvasSpaceNodeBoundingBox();
		canvasSpaceBox.position = canvasToProportionalPos(canvasSpaceBox.position);
		canvasSpaceBox.size = canvasToProportionalSize(canvasSpaceBox.size);
		return canvasSpaceBox;
	}

}
