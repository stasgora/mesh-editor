package sgora.mesh.editor.services;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.geom.Triangle;
import sgora.mesh.editor.triangulation.NodeUtils;
import sgora.mesh.editor.triangulation.TriangulationService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MeshBox implements MouseListener {

	private Integer draggedNodeIndex;
	
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

	private Integer findNodeIndex(Point position) {
		Point[] nodes = nodeUtils.getPixelMeshNodes();
		int nodeTouchDist = project.mesh.get().nodeRadius.get();
		for (int i = nodes.length - 1; i >= 0; i--) {
			Point dist = new Point(nodes[i]).subtract(position).abs();
			if (dist.x <= nodeTouchDist && dist.y <= nodeTouchDist) {
				return i;
			}
		}
		return null;
	}

	private void removeNode(Point mousePos) {
		Integer nodeIndex = findNodeIndex(mousePos);
		if(nodeIndex != null) {
			project.mesh.get().removeNode(nodeIndex);
		}
	}

	private Point clampPixelNodePos(Point node) {
		Rectangle box = nodeUtils.getPixelNodeBoundingBox();
		return node.clamp(box.position, new Point(box.position).add(box.size));
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton mouseButton) {
		draggedNodeIndex = findNodeIndex(mousePos);
		if(draggedNodeIndex != null) {
			if(mouseButton == meshBoxModel.removeNodeButton) {
				removeNode(mousePos);
			} else if(mouseButton == meshBoxModel.moveNodeButton) {
				mouseCursor.setValue(Cursor.CLOSED_HAND);
				draggedNodeIndex = findNodeIndex(mousePos);
			}
		}
		project.mesh.get().notifyListeners();
	}

	@Override
	public void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		if(draggedNodeIndex == null || button != meshBoxModel.moveNodeButton) {
			return;
		}
		Point node = project.mesh.get().getNode(draggedNodeIndex);
		Point newNodePos = clampPixelNodePos(mousePos.clamp(mainViewSize));
		node.set(nodeUtils.getNodeRelativePos(newNodePos));
		project.mesh.get().notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNodeIndex == null && mouseButton == meshBoxModel.placeNodeButton) {
			triangulationService.addNode(nodeUtils.getNodeRelativePos(clampPixelNodePos(mousePos)));
		}
		draggedNodeIndex = null;
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
