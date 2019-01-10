package sgora.mesh.editor.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.data.Point;

import java.util.List;

public class MeshCanvas extends Canvas {

	private GraphicsContext gc = getGraphicsContext2D();

	private Color NODE_COLOR = new Color(0.1, 0.2, 1, 1);
	private int NODE_RADIUS = 8;

	public void drawMesh(List<Point> nodes) {
		if(!isVisible() || nodes == null)
			return;
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.setFill(NODE_COLOR);
		for (Point node : nodes) {
			gc.fillOval(node.x - NODE_RADIUS / 2d, node.y - NODE_RADIUS / 2d, NODE_RADIUS, NODE_RADIUS);
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

}
