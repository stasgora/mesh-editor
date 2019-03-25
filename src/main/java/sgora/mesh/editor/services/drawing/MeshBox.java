package sgora.mesh.editor.services.drawing;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.MeshBoxModel;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.services.triangulation.NodeUtils;

public class MeshBox implements MouseListener {

	private Point draggedNode;
	
	private final SettableObservable<Mesh> mesh;
	private final MeshBoxModel meshBoxModel;
	private final Point canvasViewSize;
	private final ObjectProperty<Cursor> mouseCursor;
	private TriangulationService triangulationService;
	private NodeUtils nodeUtils;

	public MeshBox(SettableObservable<Mesh> mesh, MeshBoxModel meshBoxModel, Point canvasViewSize, ObjectProperty<Cursor> mouseCursor,
	               TriangulationService triangulationService, NodeUtils nodeUtils) {
		this.mesh = mesh;
		this.meshBoxModel = meshBoxModel;
		this.canvasViewSize = canvasViewSize;
		this.mouseCursor = mouseCursor;
		this.triangulationService = triangulationService;
		this.nodeUtils = nodeUtils;
	}

	private Point clampCanvasSpaceNodePos(Point node) {
		Rectangle box = nodeUtils.getCanvasSpaceNodeBoundingBox();
		return node.clamp(box.position, new Point(box.position).add(box.size));
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton mouseButton) {
		Point proportionalPos = nodeUtils.canvasToProportionalPos(mousePos);
		if(mouseButton == meshBoxModel.removeNodeButton) {
			triangulationService.removeNode(proportionalPos);
		} else if(mouseButton == meshBoxModel.moveNodeButton) {
			draggedNode = triangulationService.findNodeByLocation(proportionalPos);
			if(draggedNode != null) {
				mouseCursor.setValue(Cursor.CLOSED_HAND);
			}
		}
	}

	@Override
	public void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		if(draggedNode == null || button != meshBoxModel.moveNodeButton) {
			return;
		}
		Point newNodePos = clampCanvasSpaceNodePos(mousePos.clamp(canvasViewSize));
		triangulationService.moveNode(draggedNode, nodeUtils.canvasToProportionalPos(newNodePos));
		mesh.get().notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNode == null && mouseButton == meshBoxModel.placeNodeButton && nodeUtils.getCanvasSpaceNodeBoundingBox().contains(mousePos)) {
			triangulationService.addNode(nodeUtils.canvasToProportionalPos(mousePos));
		}
		draggedNode = null;
		mouseCursor.setValue(mousePos.isBetween(new Point(), canvasViewSize) ? Cursor.CROSSHAIR : Cursor.DEFAULT);
	}

	@Override
	public void onZoom(double amount, Point mousePos) {

	}

	@Override
	public void onMouseEnter(boolean isDragging) {
		if(!isDragging) {
			mouseCursor.setValue(Cursor.CROSSHAIR);
		}
	}

	@Override
	public void onMouseExit(boolean isDragging) {
		if(!isDragging) {
			mouseCursor.setValue(Cursor.DEFAULT);
		}
	}

}
