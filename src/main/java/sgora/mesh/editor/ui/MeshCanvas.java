package sgora.mesh.editor.ui;

import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;

public class MeshCanvas extends Canvas {

	public void draw(Mesh mesh, Point[] nodes, Rectangle nodeArea) {
		if(!isVisible()) {
			return;
		}
		context.setFill(mesh.nodeColor.get().getFXColor());
		for (Point node : nodes) {
			context.fillOval(node.x - mesh.nodeRadius.get() / 2d, node.y - mesh.nodeRadius.get() / 2d, mesh.nodeRadius.get(), mesh.nodeRadius.get());
		}
		context.setStroke(Color.gray(0.8));
		context.setLineDashes(10, 15);
		context.strokeRect(nodeArea.position.x, nodeArea.position.y, nodeArea.size.x, nodeArea.size.y);
	}

}
