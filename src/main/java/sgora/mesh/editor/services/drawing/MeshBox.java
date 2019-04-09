package sgora.mesh.editor.services.drawing;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.MouseConfig;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.services.triangulation.NodeUtils;

public class MeshBox implements MouseListener {

	private Point draggedNode;
	
	private final SettableObservable<Mesh> mesh;
	private final MouseConfig mouseConfig;
	private final Point canvasViewSize;
	private final ObjectProperty<Cursor> mouseCursor;
	private TriangulationService triangulationService;
	private NodeUtils nodeUtils;

	public MeshBox(SettableObservable<Mesh> mesh, MouseConfig mouseConfig, Point canvasViewSize, ObjectProperty<Cursor> mouseCursor,
	               TriangulationService triangulationService, NodeUtils nodeUtils) {
		this.mesh = mesh;
		this.mouseConfig = mouseConfig;
		this.canvasViewSize = canvasViewSize;
		this.mouseCursor = mouseCursor;
		this.triangulationService = triangulationService;
		this.nodeUtils = nodeUtils;
	}

	private Point clampCanvasSpaceNodePos(Point node) {
		Rectangle box = nodeUtils.getCanvasSpaceNodeBoundingBox();
		return node.clamp(box.position, new Point(box.position).add(box.size));
	}

	public void onMouseMove(Point mousePos) {
		Point proportionalPos = nodeUtils.canvasToProportionalPos(mousePos);
		draggedNode = triangulationService.findNodeByLocation(proportionalPos);
		mouseCursor.setValue(draggedNode != null ? Cursor.HAND : mouseConfig.defaultCanvasCursor);
	}

	@Override
	public boolean onDragStart(Point mousePos, MouseButton mouseButton) {
		Point proportionalPos = nodeUtils.canvasToProportionalPos(mousePos);
		if(mouseButton == mouseConfig.removeNodeButton && triangulationService.removeNode(proportionalPos)) {
			return true;
		}
		if(mouseButton == mouseConfig.moveNodeButton && draggedNode != null) {
			mouseCursor.setValue(Cursor.CLOSED_HAND);
			return true;
		}
		if(mouseButton == mouseConfig.placeNodeButton) {
			return true;
		}
		return false;
	}

	@Override
	public void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		if(draggedNode == null || button != mouseConfig.moveNodeButton) {
			return;
		}
		Point newNodePos = clampCanvasSpaceNodePos(mousePos.clamp(canvasViewSize));
		triangulationService.moveNode(draggedNode, nodeUtils.canvasToProportionalPos(newNodePos));
		mesh.get().notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNode == null && mouseButton == mouseConfig.placeNodeButton && nodeUtils.getCanvasSpaceNodeBoundingBox().contains(mousePos)) {
			triangulationService.addNode(nodeUtils.canvasToProportionalPos(mousePos));
		}
		draggedNode = null;
		mouseCursor.setValue(mousePos.isBetween(new Point(), canvasViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

}
