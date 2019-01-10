package sgora.mesh.editor.model.domain;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sgora.mesh.editor.model.data.Mesh;
import sgora.mesh.editor.model.data.Point;

import java.util.List;

public class MeshBox {

	private Mesh mesh = new Mesh();

	private static final int NODE_TOUCH_DIST = 10;

	public void onMouseClick(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(event.getButton() == MouseButton.SECONDARY) {
			eraseNodes(mousePos);
		} else if (event.getButton() == MouseButton.PRIMARY) {
			mesh.addNode(mousePos);
		}
		mesh.notifyListeners();
	}

	private void eraseNodes(Point mousePos) {
		List<Point> nodes = mesh.getNodes();
		for (int i = nodes.size() - 1; i >= 0; i--) {
			Point dist = new Point(nodes.get(i)).subtract(mousePos).abs();
			if (dist.x <= NODE_TOUCH_DIST && dist.y <= NODE_TOUCH_DIST) {
				mesh.removeNode(i);
			}
		}
	}

	public Mesh getMesh() {
		return mesh;
	}

}
