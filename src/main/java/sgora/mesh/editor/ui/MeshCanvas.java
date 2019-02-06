package sgora.mesh.editor.ui;

import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.geom.Point;

public class MeshCanvas extends Canvas {

	public void draw(MeshBoxModel meshBox, Point[] nodes) {
		if(!isVisible())
			return;
		context.setFill(meshBox.nodeColor.get());
		for (Point node : nodes) {
			context.fillOval(node.x - meshBox.nodeRadius.get() / 2d, node.y - meshBox.nodeRadius.get() / 2d, meshBox.nodeRadius.get(), meshBox.nodeRadius.get());
		}
	}

}
