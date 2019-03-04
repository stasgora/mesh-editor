package sgora.mesh.editor.services;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.services.triangulation.NodeUtils;

public class MeshBox implements MouseListener {

	private Point draggedNode;
	
	private final Project project;
	private final MeshBoxModel meshBoxModel;
	private final Point mainViewSize;
	private final ObjectProperty<Cursor> mouseCursor;
	private TriangulationService triangulationService;
	private NodeUtils nodeUtils;

	public MeshBox(Project project, MeshBoxModel meshBoxModel, Point mainViewSize, ObjectProperty<Cursor> mouseCursor,
	               TriangulationService triangulationService, NodeUtils nodeUtils) {
		this.project = project;
		this.meshBoxModel = meshBoxModel;
		this.mainViewSize = mainViewSize;
		this.mouseCursor = mouseCursor;
		this.triangulationService = triangulationService;
		this.nodeUtils = nodeUtils;
	}

	private Point clampPixelNodePos(Point node) {
		Rectangle box = nodeUtils.getPixelNodeBoundingBox();
		return node.clamp(box.position, new Point(box.position).add(box.size));
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton mouseButton) {
		if(mouseButton == meshBoxModel.removeNodeButton) {
			triangulationService.removeNode(nodeUtils.getNodeRelativePos(mousePos));
		} else if(mouseButton == meshBoxModel.moveNodeButton) {
			draggedNode = triangulationService.findNodeByLocation(mousePos);
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
		triangulationService.moveNode(draggedNode);
		Point newNodePos = clampPixelNodePos(mousePos.clamp(mainViewSize));
		draggedNode.set(nodeUtils.getNodeRelativePos(newNodePos));
		project.mesh.get().notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNode == null && mouseButton == meshBoxModel.placeNodeButton && nodeUtils.getPixelNodeBoundingBox().contains(mousePos)) {
			triangulationService.addNode(nodeUtils.getNodeRelativePos(mousePos));
		}
		draggedNode = null;
		mouseCursor.setValue(mousePos.isBetween(new Point(), mainViewSize) ? Cursor.CROSSHAIR : Cursor.DEFAULT);
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
