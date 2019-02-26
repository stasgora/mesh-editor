package sgora.mesh.editor.services.triangulation;

import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.observables.SettableObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NodeUtils {

	private static final Logger LOGGER = Logger.getLogger(NodeUtils.class.getName());

	private final AppConfigReader appConfig;
	private final Rectangle imageBox;
	private SettableObservable<Mesh> mesh;

	public NodeUtils(AppConfigReader appConfig, Rectangle imageBox, SettableObservable<Mesh> mesh) {
		this.appConfig = appConfig;
		this.imageBox = imageBox;
		this.mesh = mesh;
	}

	Point getClosestNode(Point location, Triangle triangle) {
		double nodeBoxRadius = appConfig.getDouble("meshBox.nodeBoxRadius");
		for (Point node : triangle.nodes) {
			Point dist = new Point(node).subtract(location).abs();
			if (dist.x <= nodeBoxRadius && dist.y <= nodeBoxRadius) {
				return node;
			}
		}
		return null;
	}

	List<Point> collectNodeNeighbours(Point node, Triangle firstTriangle) {
		Triangle currentTriangle = firstTriangle;
		List<Point> neighbours = new ArrayList<>();
		do {
			int nodeIndex = Arrays.asList(currentTriangle.nodes).indexOf(node);
			if(nodeIndex == -1) {
				LOGGER.warning("triangle " + currentTriangle + " does not contain given node " + node);
			}
			neighbours.add(currentTriangle.nodes[(nodeIndex + 1) % 3]);
			currentTriangle = currentTriangle.triangles[nodeIndex];
		} while (currentTriangle != firstTriangle);
		return neighbours;
	}

	public Point[] getPixelMeshNodes() {
		return mesh.get().getNodes().stream().map(this::getNodePixelPos).toArray(Point[]::new);
	}

	public Point getNodePixelPos(Point node) {
		return new Point(node).multiplyByScalar(imageBox.size.x).add(imageBox.position);
	}

	public Point getNodeRelativePos(Point node) {
		return new Point(node).subtract(imageBox.position).divideByScalar(imageBox.size.x);
	}

	public Rectangle getPixelNodeBoundingBox() {
		double spaceAroundImage = appConfig.getDouble("meshBox.spaceAroundImage");
		Rectangle area = new Rectangle();
		Point boxSize = imageBox.size;
		area.position = new Point(imageBox.position).subtract(new Point(boxSize).multiplyByScalar(spaceAroundImage));
		area.size = new Point(boxSize).multiplyByScalar(spaceAroundImage * 2 + 1);
		return area;
	}

	Point[] getBoundingNodes() {
		Rectangle boundingBox = getRelativeNodeBoundingBox();
		double majorLength = Math.max(boundingBox.size.x, boundingBox.size.y) + 1;
		return new Point[] {
			new Point(1d / 2d, -majorLength),
			new Point(boundingBox.position.x - majorLength, boundingBox.size.y + 1),
			new Point(boundingBox.position.x + boundingBox.size.x + majorLength, boundingBox.size.y + 1)
		};
	}

	private Rectangle getRelativeNodeBoundingBox() {
		double spaceAroundImage = appConfig.getDouble("meshBox.spaceAroundImage");
		double relHeight = imageBox.size.y / imageBox.size.x;
		Rectangle area = new Rectangle();
		area.position = new Point(-1, -relHeight).multiplyByScalar(spaceAroundImage);
		area.size = new Point(1, relHeight).multiplyByScalar(spaceAroundImage * 2 + 1);
		return area;
	}

}
