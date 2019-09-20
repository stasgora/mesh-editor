package stasgora.mesh.editor.services.drawing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.stasgora.observetree.SettableObservable;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.model.project.CanvasUI;
import stasgora.mesh.editor.services.history.ActionHistoryService;
import stasgora.mesh.editor.services.history.actions.node.AddNodeAction;
import stasgora.mesh.editor.services.history.actions.node.MoveNodeAction;
import stasgora.mesh.editor.services.history.actions.node.RemoveNodeAction;
import stasgora.mesh.editor.services.input.MouseListener;
import stasgora.mesh.editor.services.mesh.generation.NodeUtils;
import stasgora.mesh.editor.services.mesh.generation.TriangulationService;

@Singleton
public class MeshBox implements MouseListener {

	private Point draggedNode;
	private Point draggedNodeStartPosition;

	private final SettableObservable<Mesh> mesh;
	private CanvasUI canvasUI;
	private final TriangulationService triangulationService;
	private final NodeUtils nodeUtils;
	private final ActionHistoryService actionHistoryService;

	@Inject
	MeshBox(CanvasData canvasData, CanvasUI canvasUI, TriangulationService triangulationService, NodeUtils nodeUtils, ActionHistoryService actionHistoryService) {
		this.mesh = canvasData.mesh;
		this.canvasUI = canvasUI;
		this.triangulationService = triangulationService;
		this.nodeUtils = nodeUtils;
		this.actionHistoryService = actionHistoryService;
	}

	private Point clampCanvasSpaceNodePos(Point node) {
		Rectangle box = nodeUtils.getCanvasSpaceNodeBoundingBox();
		return node.clamp(box.getPosition(), new Point(box.getPosition()).add(box.getSize()));
	}

	public void onMouseMove(Point mousePos) {
		Point proportionalPos = nodeUtils.canvasToProportionalPos(mousePos);
		draggedNode = triangulationService.findNodeByLocation(proportionalPos);
		canvasUI.canvasMouseCursor.setValue(draggedNode != null ? Cursor.HAND : canvasUI.mouseConfig.defaultCanvasCursor);
	}

	@Override
	public boolean onDragStart(Point mousePos, MouseButton mouseButton) {
		Point proportionalPos = nodeUtils.canvasToProportionalPos(mousePos);
		if (mouseButton == canvasUI.mouseConfig.removeNodeButton && triangulationService.removeNode(proportionalPos)) {
			actionHistoryService.registerAction(new RemoveNodeAction(proportionalPos.getX(), proportionalPos.getY()));
			return true;
		}
		if (mouseButton == canvasUI.mouseConfig.moveNodeButton && draggedNode != null) {
			canvasUI.canvasMouseCursor.setValue(Cursor.CLOSED_HAND);
			draggedNodeStartPosition = new Point(draggedNode);
			return true;
		}
		return mouseButton == canvasUI.mouseConfig.placeNodeButton;
	}

	@Override
	public void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		dragPoint(mousePos, button, false);
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		dragPoint(mousePos, mouseButton, true);
		Point point = null;
		if (draggedNode == null && mouseButton == canvasUI.mouseConfig.placeNodeButton && nodeUtils.getCanvasSpaceNodeBoundingBox().contains(mousePos)) {
			point = nodeUtils.canvasToProportionalPos(mousePos);
			if (triangulationService.addNode(point))
				actionHistoryService.registerAction(new AddNodeAction(point.getX(), point.getY()));
		}
		draggedNode = point;
		canvasUI.canvasMouseCursor.setValue(mousePos.isBetween(new Point(), canvasUI.canvasViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

	private void dragPoint(Point mousePos, MouseButton button, boolean dragFinished) {
		if (draggedNode == null || button != canvasUI.mouseConfig.moveNodeButton) {
			return;
		}
		Point targetPos = nodeUtils.canvasToProportionalPos(clampCanvasSpaceNodePos(mousePos.clamp(canvasUI.canvasViewSize)));
		Point newPos = triangulationService.moveNode(draggedNode, targetPos);
		if (dragFinished)
			actionHistoryService.registerAction(new MoveNodeAction(newPos, draggedNodeStartPosition));
		mesh.get().notifyListeners();
	}

}
