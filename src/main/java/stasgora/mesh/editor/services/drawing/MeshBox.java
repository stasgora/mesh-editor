package stasgora.mesh.editor.services.drawing;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import stasgora.mesh.editor.interfaces.MouseListener;
import stasgora.mesh.editor.interfaces.TriangulationService;
import stasgora.mesh.editor.interfaces.action.history.ActionHistoryService;
import stasgora.mesh.editor.model.geom.Mesh;
import io.github.stasgora.observetree.SettableObservable;
import stasgora.mesh.editor.model.MouseConfig;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.Rectangle;
import stasgora.mesh.editor.services.history.actions.node.AddNodeAction;
import stasgora.mesh.editor.services.history.actions.node.MoveNodeAction;
import stasgora.mesh.editor.services.history.actions.node.RemoveNodeAction;
import stasgora.mesh.editor.services.triangulation.NodeUtils;

public class MeshBox implements MouseListener {

	private Point draggedNode;
	private Point draggedNodeStartPosition;
	
	private final SettableObservable<Mesh> mesh;
	private final MouseConfig mouseConfig;
	private final Point canvasViewSize;
	private final ObjectProperty<Cursor> mouseCursor;
	private TriangulationService triangulationService;
	private NodeUtils nodeUtils;
	private ActionHistoryService actionHistoryService;

	public MeshBox(SettableObservable<Mesh> mesh, MouseConfig mouseConfig, Point canvasViewSize, ObjectProperty<Cursor> mouseCursor,
	               TriangulationService triangulationService, NodeUtils nodeUtils, ActionHistoryService actionHistoryService) {
		this.mesh = mesh;
		this.mouseConfig = mouseConfig;
		this.canvasViewSize = canvasViewSize;
		this.mouseCursor = mouseCursor;
		this.triangulationService = triangulationService;
		this.nodeUtils = nodeUtils;
		this.actionHistoryService = actionHistoryService;
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
			actionHistoryService.registerAction(new RemoveNodeAction(proportionalPos.x, proportionalPos.y));
			return true;
		}
		if(mouseButton == mouseConfig.moveNodeButton && draggedNode != null) {
			mouseCursor.setValue(Cursor.CLOSED_HAND);
			draggedNodeStartPosition = new Point(draggedNode);
			return true;
		}
		if(mouseButton == mouseConfig.placeNodeButton) {
			return true;
		}
		return false;
	}

	@Override
	public void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		dragPoint(mousePos, button, false);
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		dragPoint(mousePos, mouseButton, true);
		if(draggedNode == null && mouseButton == mouseConfig.placeNodeButton && nodeUtils.getCanvasSpaceNodeBoundingBox().contains(mousePos)) {
			Point point = nodeUtils.canvasToProportionalPos(mousePos);
			if(triangulationService.addNode(point))
				actionHistoryService.registerAction(new AddNodeAction(point.x, point.y));
		}
		draggedNode = null;
		mouseCursor.setValue(mousePos.isBetween(new Point(), canvasViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

	private void dragPoint(Point mousePos, MouseButton button, boolean dragFinished) {
		if(draggedNode == null || button != mouseConfig.moveNodeButton) {
			return;
		}
		Point targetPos = nodeUtils.canvasToProportionalPos(clampCanvasSpaceNodePos(mousePos.clamp(canvasViewSize)));
		Point newPos = triangulationService.moveNode(draggedNode, targetPos);
		if(dragFinished)
			actionHistoryService.registerAction(new MoveNodeAction(newPos, draggedNodeStartPosition));
		mesh.get().notifyListeners();
	}

}
