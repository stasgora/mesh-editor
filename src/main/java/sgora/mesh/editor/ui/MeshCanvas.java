package sgora.mesh.editor.ui;

import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Point;

public class MeshCanvas extends Canvas {

	public void draw(Mesh mesh, Point[] nodes) {
		if(!isVisible()) {
			return;
		}
		context.setFill(mesh.nodeColor.get().getFXColor());
		for (Point node : nodes) {
			context.fillOval(node.x - mesh.nodeRadius.get() / 2d, node.y - mesh.nodeRadius.get() / 2d, mesh.nodeRadius.get(), mesh.nodeRadius.get());
		}
	}

}
