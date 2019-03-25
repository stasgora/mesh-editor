package sgora.mesh.editor.services.drawing;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.paint.SerializableColor;
import sgora.mesh.editor.services.triangulation.NodeUtils;

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
		List<SerializableColor> colors = Arrays.stream(triangle).map(node -> getNodeColor(node)).collect(Collectors.toList());
		double r = colors.stream().mapToDouble(color -> color.red).average().orElse(255);
		double g = colors.stream().mapToDouble(color -> color.green).average().orElse(255);
		double b = colors.stream().mapToDouble(color -> color.blue).average().orElse(255);
		return new SerializableColor(r, g, b, 1);
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
