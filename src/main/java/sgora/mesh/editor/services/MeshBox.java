package sgora.mesh.editor.services;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.triangulation.TriangulationService;

public class MeshBox implements MouseListener {

	private Integer draggedNodeIndex;
	
	private final Project project;
	private final MeshBoxModel meshBoxModel;
	private final Point mainViewSize;
	private final ObjectProperty<Cursor> mouseCursor;
	private TriangulationService triangulationService;
	private AppConfigReader appConfig;

	public MeshBox(Project project, MeshBoxModel meshBoxModel, Point mainViewSize, ObjectProperty<Cursor> mouseCursor, TriangulationService triangulationService, AppConfigReader appConfig) {
		this.project = project;
		this.meshBoxModel = meshBoxModel;
		this.mainViewSize = mainViewSize;
		this.mouseCursor = mouseCursor;
		this.triangulationService = triangulationService;
		this.appConfig = appConfig;
	}

	private Integer findNodeIndex(Point position) {
		Point[] nodes = getPixelMeshNodes();
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

	public Point[] getPixelMeshNodes() {
		return project.mesh.get().getNodes().stream().map(this::getNodePixelPos).toArray(Point[]::new);
	}

	private Point getNodePixelPos(Point node) {
		return new Point(node).multiplyByScalar(project.imageBox.size.x).add(project.imageBox.position);
	}

	private Point getNodeRelativePos(Point node) {
		return new Point(node).subtract(project.imageBox.position).divideByScalar(project.imageBox.size.x);
	}

	private Point clampPixelNodePos(Point node) {
		double spaceAroundImage = appConfig.getDouble("meshBox.spaceAroundImage");
		Point minNodePos = new Point(project.imageBox.position).subtract(new Point(project.imageBox.size).multiplyByScalar(spaceAroundImage));
		Point maxNodePos = new Point(project.imageBox.position).add(new Point(project.imageBox.size).multiplyByScalar(spaceAroundImage + 1));
		return node.clamp(minNodePos, maxNodePos);
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
		node.set(getNodeRelativePos(newNodePos));
		project.mesh.get().notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton mouseButton) {
		if(draggedNodeIndex == null && mouseButton == meshBoxModel.placeNodeButton) {
			project.mesh.get().addNode(getNodeRelativePos(clampPixelNodePos(mousePos)));
		}
		draggedNodeIndex = null;
		mouseCursor.setValue(mousePos.isBetween(new Point(), mainViewSize) ? Cursor.CROSSHAIR : Cursor.DEFAULT);
		project.mesh.get().notifyListeners();
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
