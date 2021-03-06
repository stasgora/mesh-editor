package dev.sgora.mesheditor.services.drawing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.stasgora.observetree.SettableProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.model.paint.SerializableColor;
import dev.sgora.mesheditor.model.project.CanvasData;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigReader;
import dev.sgora.mesheditor.services.config.annotation.AppConfig;
import dev.sgora.mesheditor.services.mesh.generation.NodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Singleton
public class ColorUtils {

	private final NodeUtils nodeUtils;
	private final SettableProperty<Image> baseImage;
	private final AppConfigReader appConfig;

	private static final Color OUTSIDE_IMAGE_COLOR = Color.WHITE;

	@Inject
	ColorUtils(NodeUtils nodeUtils, CanvasData canvasData, @AppConfig AppConfigReader appConfig) {
		this.nodeUtils = nodeUtils;
		this.baseImage = canvasData.baseImage;
		this.appConfig = appConfig;
	}

	public SerializableColor getPolygonColor(Point[] polygon) {
		return getAverageNodeColor(getPolygonSamplePoints(polygon));
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
		node = nodeUtils.proportionalToPixelPos(node);
		Point pixelImgSize = new Point(baseImage.get().getWidth(), baseImage.get().getHeight());
		Color color = node.isBetween(new Point(), pixelImgSize) ? baseImage.get().getPixelReader().getColor((int) node.getX(), (int) node.getY()) : OUTSIDE_IMAGE_COLOR;
		return new SerializableColor(color);
	}

	private SerializableColor getAverageNodeColor(List<Point> points) {
		List<SerializableColor> colors = points.stream().map(this::getNodeColor).collect(Collectors.toList());
		double r = colors.stream().mapToDouble(color -> color.getRed()).average().orElse(255);
		double g = colors.stream().mapToDouble(color -> color.getGreen()).average().orElse(255);
		double b = colors.stream().mapToDouble(color -> color.getBlue()).average().orElse(255);
		return new SerializableColor(r, g, b, 1);
	}

	private List<Point> getPolygonSamplePoints(Point[] vertices) {
		List<Point> points = new ArrayList<>();
		int subdivisions = appConfig.getInt("meshBox.edgeColorSamples");
		for (int i = 0; i < vertices.length; i++) {
			points.add(new Point(vertices[i]));
			points.addAll(subdivideSegment(vertices[(i + 1) % vertices.length], vertices[i], subdivisions));
		}
		int margin = subdivisions + 2;
		for (int i = points.size() - margin; i >= margin; i--)
			points.addAll(subdivideSegment(points.get(0), points.get(i), subdivisions));
		return points;
	}

	private List<Point> subdivideSegment(Point a, Point b, int subdivisions) {
		Point baseVector = new Point(b).subtract(a);
		return IntStream.rangeClosed(1, subdivisions).mapToObj(i -> new Point(baseVector).multiplyByScalar(i / (double) (subdivisions + 1)).add(a)).collect(Collectors.toList());
	}

}
