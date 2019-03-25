package sgora.mesh.editor.ui.canvas;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.paint.SerializableColor;
import sgora.mesh.editor.model.project.VisualProperties;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.drawing.ColorUtils;

import java.util.List;

public class MeshCanvas extends Canvas {

	private ColorUtils colorUtils;
	private SettableProperty<Image> baseImage;
	private VisualProperties visualProperties;

	public void init(ColorUtils colorUtils, SettableProperty<Image> baseImage, VisualProperties visualProperties) {
		this.colorUtils = colorUtils;
		this.baseImage = baseImage;
		this.visualProperties = visualProperties;
	}

	public void draw(Point[] nodes, List<Point[]> triangles, Rectangle boundingBox) {
		if(!isVisible()) {
			return;
		}
		VisualProperties properties = this.visualProperties;
		double transparency = properties.meshTransparency.get();

		for (Point[] triangle : triangles) {
			context.setFill(colorUtils.getTriangleColor(triangle).setAlpha(transparency).getFXColor());
			context.beginPath();
			context.moveTo(triangle[0].x, triangle[0].y);
			context.lineTo(triangle[1].x, triangle[1].y);
			context.lineTo(triangle[2].x, triangle[2].y);
			context.closePath();
			context.fill();
		}
		if(visualProperties.nodesVisible.get()) {
			Integer nodeRadius = properties.nodeRadius.get();
			for (Point node : nodes) {
				context.setFill(colorUtils.getNodeColor(node).setAlpha(transparency).getFXColor());
				context.fillOval(node.x - nodeRadius / 2d, node.y - nodeRadius / 2d, nodeRadius, nodeRadius);
			}
		}
		context.setStroke(Color.gray(0.8));
		context.setLineDashes(10, 15);
		context.strokeRect(boundingBox.position.x, boundingBox.position.y, boundingBox.size.x, boundingBox.size.y);
	}

}
