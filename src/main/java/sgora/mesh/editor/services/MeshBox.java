package sgora.mesh.editor.services;

import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.containers.ProjectModel;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.interfaces.MouseListener;

public class MeshBox implements MouseListener {

	private final Model model;

	private Integer draggedNodeIndex;

	public MeshBox(Model model) {
		this.model = model;
	}
	
	private ProjectModel project() {
		return model.project;
	}

	private Integer findNodeIndex(Point position) {
		Point[] nodes = getPixelMeshNodes();
		int nodeTouchDist = project().mesh.get().nodeRadius.get();
		for (int i = nodes.length - 1; i >= 0; i--) {
			Point dist = new Point(nodes[i]).subtract(position).abs();
			if (dist.x <= nodeTouchDist && dist.y <= nodeTouchDist)
				return i;
		}
		return null;
	}

	private void removeNode(Point mousePos) {
		Integer nodeIndex = findNodeIndex(mousePos);
		if(nodeIndex != null)
			project().mesh.get().removeNode(nodeIndex);
	}

	public Point[] getPixelMeshNodes() {
		return project().mesh.get().getNodes().stream().map(this::getNodePixelPos).toArray(Point[]::new);
	}

	private Point getNodePixelPos(Point node) {
		return new Point(node).multiply(model.imageBox.size).add(model.imageBox.position);
	}

	private Point getNodeRelativePos(Point node) {
		return new Point(node).subtract(model.imageBox.position).divide(model.imageBox.size);
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton mouseButton) {
		draggedNodeIndex = findNodeIndex(mousePos);
		if(draggedNodeIndex != null) {
			if(mouseButton == model.meshBoxModel.removeNodeButton)
				removeNode(mousePos);
			else if(mouseButton == model.meshBoxModel.moveNodeButton) {
				model.mouseCursor.setValue(Cursor.CLOSED_HAND);
				draggedNodeIndex = findNodeIndex(mousePos);
			}
		}
		project().mesh.get().notifyListeners();
	}

	@Override
	public void onMouseDrag(Point dragAmount, MouseButton button) {
		if(draggedNodeIndex == null || button != model.meshBoxModel.moveNodeButton)
			return;
		Point node = project().mesh.get().getNode(draggedNodeIndex);
		node.set(getNodeRelativePos(getNodePixelPos(node).add(dragAmount)));
		project().mesh.get().notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNodeIndex == null && mouseButton == model.meshBoxModel.placeNodeButton)
			project().mesh.get().addNode(getNodeRelativePos(mousePos));
		draggedNodeIndex = null;
		model.mouseCursor.setValue(mousePos.isBetween(new Point(), model.mainViewSize) ? Cursor.CROSSHAIR : Cursor.DEFAULT);
		project().mesh.get().notifyListeners();
	}

	@Override
	public void onZoom(double amount, Point mousePos) {

	}

	@Override
	public void onMouseEnter(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.CROSSHAIR);
	}

	@Override
	public void onMouseExit(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.DEFAULT);
	}

}
