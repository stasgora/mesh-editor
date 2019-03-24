package sgora.mesh.editor.services.drawing;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.paint.SerializableColor;
import sgora.mesh.editor.services.triangulation.NodeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {

	private NodeUtils nodeUtils;

	public ColorUtils(NodeUtils nodeUtils) {
		this.nodeUtils = nodeUtils;
	}

	public SerializableColor getTriangleColor(Point[] triangle, PixelReader image, Point imageSize) {
		List<SerializableColor> colors = Arrays.stream(triangle).map(node -> getNodeColor(nodeUtils.canvasToPixelPos(node), image, imageSize)).collect(Collectors.toList());
		double r = colors.stream().mapToDouble(color -> color.red).average().orElse(255);
		double g = colors.stream().mapToDouble(color -> color.green).average().orElse(255);
		double b = colors.stream().mapToDouble(color -> color.blue).average().orElse(255);
		return new SerializableColor(r, g, b, 1);
	}

	private SerializableColor getNodeColor(Point node, PixelReader image, Point imageSize) {
		Color color = node.isBetween(new Point(), imageSize) ? image.getColor((int) node.x, (int) node.y) : Color.WHITE;
		return new SerializableColor(color);
	}

}
