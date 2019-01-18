package sgora.mesh.editor.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.geom.Point;

public class MeshCanvas extends Canvas {

	private GraphicsContext gc = getGraphicsContext2D();

	public void draw(MeshBoxModel meshBox, Point[] nodes) {
		if(!isVisible() || nodes == null)
			return;
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.setFill(meshBox.nodeColor.get());
		for (Point node : nodes) {
			gc.fillOval(node.x - meshBox.nodeRadius.get() / 2d, node.y - meshBox.nodeRadius.get() / 2d, meshBox.nodeRadius.get(), meshBox.nodeRadius.get());
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

}
