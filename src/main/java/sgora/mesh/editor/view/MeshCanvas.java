package sgora.mesh.editor.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import sgora.mesh.editor.model.Point;
import sgora.mesh.editor.model.Rectangle;

import java.util.List;

public class MeshCanvas extends Canvas {

	GraphicsContext gc = getGraphicsContext2D();

	private static final int NODE_RADIUS = 5;

	public void drawMesh(List<Point> nodes) {
		if(!isVisible() || nodes == null)
			return;
		gc.clearRect(0, 0, getWidth(), getHeight());
		for (Point node : nodes) {
			gc.fillOval(node.x - NODE_RADIUS / 2d, node.y - NODE_RADIUS / 2d, NODE_RADIUS, NODE_RADIUS);
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

}
