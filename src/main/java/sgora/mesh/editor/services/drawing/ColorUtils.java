package sgora.mesh.editor.services.drawing;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.services.triangulation.NodeUtils;

import java.util.Arrays;

public class ColorUtils {

	private NodeUtils nodeUtils;

	public ColorUtils(NodeUtils nodeUtils) {
		this.nodeUtils = nodeUtils;
	}

	public Color getTriangleColor(Triangle triangle, PixelReader image) {
	}

}
