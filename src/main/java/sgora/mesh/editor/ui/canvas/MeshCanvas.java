package sgora.mesh.editor.ui.canvas;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.drawing.ColorUtils;

import java.util.List;
import java.util.Random;

public class MeshCanvas extends Canvas {

	private ColorUtils colorUtils;
	private SettableProperty<Image> baseImage;

	public void init(ColorUtils colorUtils, SettableProperty<Image> baseImage) {
		this.colorUtils = colorUtils;
		this.baseImage = baseImage;
	}

	public void draw(Mesh mesh, Point[] nodes, List<Point[]> triangles, Rectangle boundingBox) {
		if(!isVisible()) {
			return;
		}
		Point pixelImgSize = new Point(baseImage.get().getWidth(), baseImage.get().getHeight());
		PixelReader pixels = baseImage.get().getPixelReader();
		for (Point[] triangle : triangles) {
			context.setFill(colorUtils.getTriangleColor(triangle, pixels, pixelImgSize));
			context.beginPath();
			context.moveTo(triangle[0].x, triangle[0].y);
			context.lineTo(triangle[1].x, triangle[1].y);
			context.lineTo(triangle[2].x, triangle[2].y);
			context.closePath();
			context.fill();
		}
		context.setFill(mesh.nodeColor.get().getFXColor());
		for (Point node : nodes) {
			context.fillOval(node.x - mesh.nodeRadius.get() / 2d, node.y - mesh.nodeRadius.get() / 2d, mesh.nodeRadius.get(), mesh.nodeRadius.get());
		}

		context.setStroke(Color.gray(0.8));
		context.setLineDashes(10, 15);
		context.strokeRect(boundingBox.position.x, boundingBox.position.y, boundingBox.size.x, boundingBox.size.y);
	}

}
