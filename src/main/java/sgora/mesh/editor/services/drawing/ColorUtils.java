package sgora.mesh.editor.services.drawing;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.paint.SerializableColor;
import sgora.mesh.editor.services.triangulation.NodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {

	private NodeUtils nodeUtils;
	private SettableProperty<Image> baseImage;

	public ColorUtils(NodeUtils nodeUtils, SettableProperty<Image> baseImage) {
		this.nodeUtils = nodeUtils;
		this.baseImage = baseImage;
	}

	public SerializableColor getTriangleColor(Point[] triangle) {
		List<SerializableColor> colors = getTriangleSamplePoints(triangle).stream().map(this::getNodeColor).collect(Collectors.toList());
		double r = colors.stream().mapToDouble(color -> color.red).average().orElse(255);
		double g = colors.stream().mapToDouble(color -> color.green).average().orElse(255);
		double b = colors.stream().mapToDouble(color -> color.blue).average().orElse(255);
		return new SerializableColor(r, g, b, 1);
	}

	private List<Point> getTriangleSamplePoints(Point[] triangle) {
		Point[] vectors = new Point[] {new Point(triangle[1]).subtract(triangle[0]), new Point(triangle[2]).subtract(triangle[1])};
		List<Point> points = new ArrayList<>();
		int subdivisions = 10;
		for (int i = 0; i <= subdivisions; i++) {
			Point baseVector = new Point(triangle[0]).add(new Point(vectors[0]).multiplyByScalar(i / (double) subdivisions));
			for (int j = 0; j <= i; j++) {
				points.add(new Point(baseVector).add(new Point(vectors[1]).multiplyByScalar(j / (double) subdivisions)));
			}
		}
		return points;
	}

	public SerializableColor getEdgeColor(Point first, Point second) {
		return getNodeColor(first).averageWith(getNodeColor(second));
	}

	public SerializableColor getNodeColor(Point node) {
		node = nodeUtils.canvasToPixelPos(node);
		Point pixelImgSize = new Point(baseImage.get().getWidth(), baseImage.get().getHeight());
		Color color = node.isBetween(new Point(), pixelImgSize) ? baseImage.get().getPixelReader().getColor((int) node.x, (int) node.y) : Color.WHITE;
		return new SerializableColor(color);
	}

}
