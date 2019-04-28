package stasgora.mesh.editor.services.drawing;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import stasgora.mesh.editor.interfaces.config.AppConfigReader;
import stasgora.mesh.editor.model.geom.Point;
import io.github.stasgora.observetree.SettableProperty;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.services.triangulation.NodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {

	private NodeUtils nodeUtils;
	private SettableProperty<Image> baseImage;
	private AppConfigReader appConfig;

	public ColorUtils(NodeUtils nodeUtils, SettableProperty<Image> baseImage, AppConfigReader appConfig) {
		this.nodeUtils = nodeUtils;
		this.baseImage = baseImage;
		this.appConfig = appConfig;
	}

	public SerializableColor getTriangleColor(Point[] triangle) {
		return getAverageNodeColor(getTriangleSamplePoints(triangle));
	}

	public SerializableColor getEdgeColor(Point first, Point second) {
		int subdivisions = appConfig.getInt("meshBox.edgeColorSamples");
		Point vector = new Point(second).subtract(first);
		List<Point> nodes = new ArrayList<>();
		for (int i = 0; i <= subdivisions; i++) {
			nodes.add(new Point(first).add(new Point(vector).multiplyByScalar(i / (double) subdivisions)));
		}
		return getAverageNodeColor(nodes);
	}

	public SerializableColor getNodeColor(Point node) {
		node = nodeUtils.canvasToPixelPos(node);
		Point pixelImgSize = new Point(baseImage.get().getWidth(), baseImage.get().getHeight());
		Color color = node.isBetween(new Point(), pixelImgSize) ? baseImage.get().getPixelReader().getColor((int) node.x, (int) node.y) : Color.WHITE;
		return new SerializableColor(color);
	}

	private SerializableColor getAverageNodeColor(List<Point> points) {
		List<SerializableColor> colors = points.stream().map(this::getNodeColor).collect(Collectors.toList());
		double r = colors.stream().mapToDouble(color -> color.red).average().orElse(255);
		double g = colors.stream().mapToDouble(color -> color.green).average().orElse(255);
		double b = colors.stream().mapToDouble(color -> color.blue).average().orElse(255);
		return new SerializableColor(r, g, b, 1);
	}

	private List<Point> getTriangleSamplePoints(Point[] triangle) {
		Point[] vectors = new Point[] {new Point(triangle[1]).subtract(triangle[0]), new Point(triangle[2]).subtract(triangle[1])};
		List<Point> points = new ArrayList<>();
		int subdivisions = appConfig.getInt("meshBox.edgeColorSamples");
		for (int i = 0; i <= subdivisions; i++) {
			Point baseVector = new Point(triangle[0]).add(new Point(vectors[0]).multiplyByScalar(i / (double) subdivisions));
			for (int j = 0; j <= i; j++) {
				points.add(new Point(baseVector).add(new Point(vectors[1]).multiplyByScalar(j / (double) subdivisions)));
			}
		}
		return points;
	}

}
