package sgora.mesh.editor.triangulation;

import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.model.observables.SettableObservable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NodeUtils {

	private final AppConfigReader appConfig;
	private final Rectangle imageBox;
	private SettableObservable<Mesh> mesh;

	public NodeUtils(AppConfigReader appConfig, Rectangle imageBox, SettableObservable<Mesh> mesh) {
		this.appConfig = appConfig;
		this.imageBox = imageBox;
		this.mesh = mesh;
	}

	public Point[] getPixelMeshNodes() {
		return mesh.get().getNodes().stream().map(this::getNodePixelPos).toArray(Point[]::new);
	}

	public List<Point[]> getPixelTriangles() {
		return mesh.get().getTriangles().stream().map(this::getPixelTriangle).collect(Collectors.toList());
	}

	private Point[] getPixelTriangle(Triangle triangle) {
		return Arrays.stream(triangle.nodes).map(node -> getNodePixelPos(new Point(node))).toArray(Point[]::new);
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
