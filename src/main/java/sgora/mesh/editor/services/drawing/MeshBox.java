package sgora.mesh.editor.services.drawing;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.KeysConfig;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.services.triangulation.NodeUtils;

public class MeshBox implements MouseListener {

	private Point draggedNode;
	
	private final SettableObservable<Mesh> mesh;
	private final KeysConfig keysConfig;
	private final Point canvasViewSize;
	private final ObjectProperty<Cursor> mouseCursor;
	private TriangulationService triangulationService;
	private NodeUtils nodeUtils;

	public MeshBox(SettableObservable<Mesh> mesh, KeysConfig keysConfig, Point canvasViewSize, ObjectProperty<Cursor> mouseCursor,
	               TriangulationService triangulationService, NodeUtils nodeUtils) {
		this.mesh = mesh;
		this.keysConfig = keysConfig;
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
	public boolean onDragStart(Point mousePos, MouseButton mouseButton) {
		Point proportionalPos = nodeUtils.canvasToProportionalPos(mousePos);
		if(mouseButton == keysConfig.removeNodeButton) {
			triangulationService.removeNode(proportionalPos);
			return true;
		} else if(mouseButton == keysConfig.moveNodeButton) {
			draggedNode = triangulationService.findNodeByLocation(proportionalPos);
			if(draggedNode != null) {
				mouseCursor.setValue(Cursor.CLOSED_HAND);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		if(draggedNode == null || button != keysConfig.moveNodeButton) {
			return false;
		}
		Point newNodePos = clampCanvasSpaceNodePos(mousePos.clamp(canvasViewSize));
		triangulationService.moveNode(draggedNode, nodeUtils.canvasToProportionalPos(newNodePos));
		mesh.get().notifyListeners();
		return true;
	}

	@Override
	public boolean onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNode == null && mouseButton == keysConfig.placeNodeButton && nodeUtils.getCanvasSpaceNodeBoundingBox().contains(mousePos)) {
			triangulationService.addNode(nodeUtils.canvasToProportionalPos(mousePos));
		}
		draggedNode = null;
		mouseCursor.setValue(mousePos.isBetween(new Point(), canvasViewSize) ? Cursor.CROSSHAIR : Cursor.DEFAULT);
		return true;
	}

	@Override
	public boolean onZoom(double amount, Point mousePos) {
		return false;
	}

	@Override
	public boolean onMouseEnter(boolean isDragging) {
		if(!isDragging) {
			mouseCursor.setValue(Cursor.CROSSHAIR);
		}
		return true;
	}

}
