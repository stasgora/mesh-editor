package sgora.mesh.editor.services.triangulation;

import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.CanvasData;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class NodeUtils {

	private static final Logger LOGGER = Logger.getLogger(NodeUtils.class.getName());

	private final AppConfigReader appConfig;
	private final SettableObservable<CanvasData> canvasData;

	private final double REL_SPACE_FACTOR;

	public NodeUtils(AppConfigReader appConfig, SettableObservable<CanvasData> canvasData) {
		this.appConfig = appConfig;
		this.canvasData = canvasData;
		REL_SPACE_FACTOR = appConfig.getDouble("meshBox.proportionalSpaceFactor");
	}

	Point getClosestNode(Point location, Triangle triangle) {
		double nodeBoxRadius = appConfig.getDouble("meshBox.nodeBoxRadius") / (canvasData.get().imageBox.size.x / REL_SPACE_FACTOR);
		for (Point node : triangle.nodes) {
			Point dist = new Point(node).subtract(location).abs();
			if (dist.x <= nodeBoxRadius && dist.y <= nodeBoxRadius) {
				return node;
			}
		}
		return null;
	}

	void getNodeNeighbours(Point node, Triangle firstTriangle, List<Point> outPoints, List<Triangle> outTriangles) {
		Triangle currentTriangle = firstTriangle;
		do {
			int nodeIndex = Arrays.asList(currentTriangle.nodes).indexOf(node);
			if(nodeIndex == -1) {
				LOGGER.warning("triangle " + currentTriangle + " does not contain given node " + node);
			}
			nodeIndex = (nodeIndex + 2) % 3;
			outPoints.add(currentTriangle.nodes[nodeIndex]);
			currentTriangle = currentTriangle.triangles[nodeIndex];
			outTriangles.add(currentTriangle);
		} while (currentTriangle != firstTriangle);
	}

	public Point[] getCanvasSpaceNodes() {
		return canvasData.get().mesh.get().getNodes().stream().map(this::proportionalToCanvasPos).toArray(Point[]::new);
	}

	public Point proportionalToCanvasPos(Point node) {
		Rectangle imageBox = canvasData.get().imageBox;
		return new Point(node).multiplyByScalar(imageBox.size.x / REL_SPACE_FACTOR).add(imageBox.position);
	}

	public Point canvasToProportionalPos(Point node) {
		Rectangle imageBox = canvasData.get().imageBox;
		return new Point(node).subtract(imageBox.position).divideByScalar(imageBox.size.x / REL_SPACE_FACTOR);
	}

	public Point canvasToProportionalSize(Point node) {
		Rectangle imageBox = canvasData.get().imageBox;
		return new Point(node).divideByScalar(imageBox.size.x / REL_SPACE_FACTOR);
	}

	public Point canvasToPixelPos(Point node) {
		CanvasData data = canvasData.get();
		return new Point(node).subtract(data.imageBox.position).divideByScalar(data.imageBox.size.x / data.baseImage.get().getWidth());
	}

	public Rectangle getCanvasSpaceNodeBoundingBox() {
		Rectangle imageBox = canvasData.get().imageBox;
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
		return new Point[] {
			new Point(boundingBox.position.x + boundingBox.size.x / 2, -majorLength),
			new Point(boundingBox.position.x - majorLength, boundingBox.size.y),
			new Point(boundingBox.position.x + boundingBox.size.x + majorLength, boundingBox.size.y)
		};
	}

	private Rectangle getProportionalNodeBoundingBox() {
		Rectangle canvasSpaceBox = getCanvasSpaceNodeBoundingBox();
		canvasSpaceBox.position = canvasToProportionalPos(canvasSpaceBox.position);
		canvasSpaceBox.size = canvasToProportionalSize(canvasSpaceBox.size);
		return canvasSpaceBox;
	}

}
