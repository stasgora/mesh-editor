package stasgora.mesh.editor.services.mesh.generation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.services.config.interfaces.AppConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class NodeUtils {

	private static final Logger LOGGER = Logger.getLogger(NodeUtils.class.getName());

	private final AppConfigReader appConfig;
	private final CanvasData canvasData;

	private final double relativeSpaceFactor;

	@Inject
	NodeUtils(@AppConfig AppConfigReader appConfig, CanvasData canvasData) {
		this.appConfig = appConfig;
		this.canvasData = canvasData;
		relativeSpaceFactor = appConfig.getDouble("meshBox.proportionalSpaceFactor");
	}

	public Triangle findNodeTriangle(Point node) {
		return canvasData.mesh.get().getTriangles().stream().filter(triangle -> Arrays.stream(triangle.getNodes()).anyMatch(vertex -> vertex == node)).findFirst().orElse(null);
	}

	Point getClosestNode(Point location, Triangle triangle) {
		double nodeBoxRadius = appConfig.getDouble("meshBox.nodeBoxRadius") / (canvasData.imageBox.getSize().getX() / relativeSpaceFactor);
		for (Point node : triangle.getNodes()) {
			Point dist = new Point(node).subtract(location).abs();
			if (dist.getX() <= nodeBoxRadius && dist.getY() <= nodeBoxRadius) {
				return node;
			}
		}
		return null;
	}

	public void getNodeNeighbours(Point node, Triangle firstTriangle, List<Point> outPoints, List<Triangle> outTriangles) {
		Triangle currentTriangle = firstTriangle;
		do {
			int nodeIndex = Arrays.asList(currentTriangle.getNodes()).indexOf(node);
			if (nodeIndex == -1) {
				LOGGER.warning(String.format("triangle %s does not contain given node %s", currentTriangle, node));
			}
			nodeIndex = (nodeIndex + 2) % 3;
			if (outPoints != null)
				outPoints.add(currentTriangle.getNodes()[nodeIndex]);
			currentTriangle = currentTriangle.getTriangles()[nodeIndex];
			if (outTriangles != null)
				outTriangles.add(currentTriangle);
		} while (currentTriangle != firstTriangle);
	}

	public Point proportionalToCanvasPos(Point node) {
		Rectangle imageBox = canvasData.imageBox;
		return new Point(node).multiplyByScalar(imageBox.getSize().getX() / relativeSpaceFactor).add(imageBox.getPosition());
	}

	public Point canvasToProportionalPos(Point node) {
		Rectangle imageBox = canvasData.imageBox;
		return new Point(node).subtract(imageBox.getPosition()).divideByScalar(imageBox.getSize().getX() / relativeSpaceFactor);
	}

	public Point canvasToProportionalSize(Point node) {
		return new Point(node).multiplyByScalar(proportionalScaleFactor());
	}

	public double proportionalScaleFactor() {
		Rectangle imageBox = canvasData.imageBox;
		return relativeSpaceFactor / imageBox.getSize().getX();
	}

	public Point proportionalToPixelPos(Point node) {
		return new Point(node).multiplyByScalar(canvasData.baseImage.get().getWidth() / relativeSpaceFactor);
	}

	public Point getProportionalMarginSize() {
		double spaceAroundImage = appConfig.getDouble("meshBox.spaceAroundImage");
		return canvasToProportionalSize(new Point(canvasData.imageBox.getSize()).multiplyByScalar(spaceAroundImage));
	}

	public Rectangle getCanvasSpaceNodeBoundingBox() {
		Rectangle imageBox = canvasData.imageBox;
		double spaceAroundImage = appConfig.getDouble("meshBox.spaceAroundImage");
		Rectangle area = new Rectangle();
		Point boxSize = imageBox.getSize();
		area.setPosition(new Point(imageBox.getPosition()).subtract(new Point(boxSize).multiplyByScalar(spaceAroundImage)));
		area.setSize(new Point(boxSize).multiplyByScalar(spaceAroundImage * 2 + 1));
		return area;
	}

	Point[] getBoundingNodes() {
		Rectangle boundingBox = getProportionalNodeBoundingBox();
		double majorLength = boundingBox.getSize().getX() + boundingBox.getSize().getY();
		return new Point[]{
				new Point(boundingBox.getPosition().getX() + boundingBox.getSize().getX() / 2, -majorLength),
				new Point(boundingBox.getPosition().getX() - majorLength, boundingBox.getSize().getY()),
				new Point(boundingBox.getPosition().getX() + boundingBox.getSize().getX() + majorLength, boundingBox.getSize().getY())
		};
	}

	public Rectangle getProportionalNodeBoundingBox() {
		Rectangle canvasSpaceBox = getCanvasSpaceNodeBoundingBox();
		canvasSpaceBox.setPosition(canvasToProportionalPos(canvasSpaceBox.getPosition()));
		canvasSpaceBox.setSize(canvasToProportionalSize(canvasSpaceBox.getSize()));
		return canvasSpaceBox;
	}

}
