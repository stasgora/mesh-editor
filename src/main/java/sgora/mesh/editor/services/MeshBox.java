package sgora.mesh.editor.services;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;

import java.util.List;

public class MeshBox {

	private final Model model;

	private final int NODE_TOUCH_DIST = 10;

	public MeshBox(Model model) {
		this.model = model;
	}

	private MeshBoxModel model() {
		return model.meshBoxModel;
	}

	public void onMouseClick(MouseEvent event) {
		if(model.imageBoxModel.baseImage == null || model.activeTool.getValue() != MouseTool.MESH_EDITOR ||
				!new Point(event.getX(), event.getY()).isBetween(new Point(), model.mainViewSize))
			return;
		Point mousePos = new Point(event.getX(), event.getY());
		if(event.getButton() == model().removeNodeButton) {
			eraseNodes(mousePos);
		} else if (event.getButton() == model().placeNodeButton) {
			model().mesh.addNode(mousePos.subtract(model.imageBoxModel.imageBox.getPosition()).divide(model.imageBoxModel.imageBox.getSize()));
		}
		model().mesh.notifyListeners();
	}

	private void eraseNodes(Point mousePos) {
		Point[] nodes = getMeshNodes();
		for (int i = nodes.length - 1; i >= 0; i--) {
			Point dist = new Point(nodes[i]).subtract(mousePos).abs();
			if (dist.x <= NODE_TOUCH_DIST && dist.y <= NODE_TOUCH_DIST) {
				model().mesh.removeNode(i);
			}
		}
	}

	public Point[] getMeshNodes() {
		return model().mesh.getNodes().stream().map(node -> new Point(node).multiply(model.imageBoxModel.imageBox.getSize())
				.add(model.imageBoxModel.imageBox.getPosition())).toArray(Point[]::new);
	}

	public void onMouseEnter() {
		if(model.activeTool.getValue() == MouseTool.MESH_EDITOR)
			model.mouseCursor.setValue(Cursor.CROSSHAIR);
	}

	public void onMouseExit() {
		if(model.activeTool.getValue() == MouseTool.MESH_EDITOR)
			model.mouseCursor.setValue(Cursor.DEFAULT);
	}
}
