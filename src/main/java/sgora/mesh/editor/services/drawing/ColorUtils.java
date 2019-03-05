package sgora.mesh.editor.services.drawing;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.services.triangulation.NodeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {

	private NodeUtils nodeUtils;

	public ColorUtils(NodeUtils nodeUtils) {
		this.nodeUtils = nodeUtils;
	}

	public Color getTriangleColor(Point[] triangle, PixelReader image, Point imageSize) {
		List<Color> colors = Arrays.stream(triangle).map(node -> getNodeColor(nodeUtils.canvasToPixelPos(node), image, imageSize)).collect(Collectors.toList());
		double r = colors.stream().mapToDouble(Color::getRed).average().orElse(255);
		double g = colors.stream().mapToDouble(Color::getGreen).average().orElse(255);
		double b = colors.stream().mapToDouble(Color::getBlue).average().orElse(255);
		return new Color(r, g, b, 0.8);
	}

	private Color getNodeColor(Point node, PixelReader image, Point imageSize) {
		return node.isBetween(new Point(), imageSize) ? image.getColor((int) node.x, (int) node.y) : Color.WHITE;
	}

}
