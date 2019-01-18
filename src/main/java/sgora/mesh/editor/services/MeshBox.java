package sgora.mesh.editor.services;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Point;

public class MeshBox {

	private final Model model;

	private Integer draggedNodeIndex;

	private final int NODE_TOUCH_DIST = 10;

	public MeshBox(Model model) {
		this.model = model;
	}

	private MeshBoxModel model() {
		return model.meshBoxModel;
	}

	private Integer findNodeIndex(Point position) {
		Point[] nodes = getMeshNodes();
		for (int i = nodes.length - 1; i >= 0; i--) {
			Point dist = new Point(nodes[i]).subtract(position).abs();
			if (dist.x <= NODE_TOUCH_DIST && dist.y <= NODE_TOUCH_DIST)
				return i;
		}
		return null;
	}

	private void removeNode(Point mousePos) {
		Integer nodeIndex = findNodeIndex(mousePos);
		if(nodeIndex != null)
			model().mesh.removeNode(nodeIndex);
	}

	public Point[] getMeshNodes() {
		return model().mesh.getNodes().stream().map(this::getNodePixelPos).toArray(Point[]::new);
	}

	private Point getNodePixelPos(Point node) {
		return new Point(node).multiply(model.imageBoxModel.imageBox.size).add(model.imageBoxModel.imageBox.position);
	}

	private Point getNodeRelativePos(Point node) {
		return new Point(node).subtract(model.imageBoxModel.imageBox.position).divide(model.imageBoxModel.imageBox.size);
	}

	public void onDragStart(Point mousePos, MouseButton mouseButton) {
		draggedNodeIndex = findNodeIndex(mousePos);
		if(draggedNodeIndex != null) {
			if(mouseButton == model().removeNodeButton)
				removeNode(mousePos);
			else if(mouseButton == model().moveNodeButton) {
				model.mouseCursor.setValue(Cursor.CLOSED_HAND);
				draggedNodeIndex = findNodeIndex(mousePos);
			}
		}
		model().mesh.notifyListeners();
	}

	public void onMouseDrag(Point dragAmount, MouseButton button) {
		if(draggedNodeIndex == null || button != model.meshBoxModel.moveNodeButton)
			return;
		Point node = model().mesh.getNode(draggedNodeIndex);
		node.set(getNodeRelativePos(getNodePixelPos(node).add(dragAmount)));
		model().mesh.notifyListeners();
	}

	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNodeIndex == null && mouseButton == model().placeNodeButton)
			model().mesh.addNode(getNodeRelativePos(mousePos));
		draggedNodeIndex = null;
		model.mouseCursor.setValue(mousePos.isBetween(new Point(), model.mainViewSize) ? Cursor.CROSSHAIR : Cursor.DEFAULT);
		model().mesh.notifyListeners();
	}

	public void onMouseEnter(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.CROSSHAIR);
	}

	public void onMouseExit(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.DEFAULT);
	}

}
