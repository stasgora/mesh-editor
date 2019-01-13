package sgora.mesh.editor.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.services.MeshBox;

import java.util.List;

public class MeshCanvas extends Canvas {

	private GraphicsContext gc = getGraphicsContext2D();

	public void draw(MeshBoxModel meshBox, Point[] nodes) {
		if(!isVisible() || nodes == null)
			return;
		gc.clearRect(0, 0, getWidth(), getHeight());
		gc.setFill(meshBox.nodeColor);
		for (Point node : nodes) {
			gc.fillOval(node.x - meshBox.nodeRadius / 2d, node.y - meshBox.nodeRadius / 2d, meshBox.nodeRadius, meshBox.nodeRadius);
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

}
