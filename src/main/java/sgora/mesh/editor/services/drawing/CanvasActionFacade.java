package sgora.mesh.editor.services.drawing;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.interfaces.CanvasAction;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.project.LoadState;

public class CanvasActionFacade implements CanvasAction {

	private final SettableObservable<LoadState> loadState;
	private final ImageBox imageBox;
	private final MeshBox meshBox;
	private final SettableProperty<MouseTool> activeTool;

	private Point lastMouseDragPoint;

	public CanvasActionFacade(SettableObservable<LoadState> loadState, ImageBox imageBox, MeshBox meshBox, SettableProperty<MouseTool> activeTool) {
		this.loadState = loadState;
		this.imageBox = imageBox;
		this.meshBox = meshBox;
		this.activeTool = activeTool;
	}

	@Override
	public void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.get().loaded.get()) {
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onDragStart(mousePos, event.getButton());
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onDragStart(mousePos, event.getButton());
			}
		}
		lastMouseDragPoint = mousePos;
	}

	@Override
	public void onMouseDrag(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		Point dragAmount = new Point(mousePos).subtract(lastMouseDragPoint);
		if(loadState.get().loaded.get()) {
			if (activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onMouseDrag(new Point(dragAmount), mousePos, event.getButton());
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onMouseDrag(new Point(dragAmount), mousePos, event.getButton());
			}
		}
		lastMouseDragPoint.set(mousePos);
	}

	@Override
	public void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.get().loaded.get()) {
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onDragEnd(new Point(mousePos), event.getButton());
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onDragEnd(new Point(mousePos), event.getButton());
			}
		}
	}

	@Override
	public void onScroll(ScrollEvent event) {
		if(loadState.get().loaded.get()) {
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
		}
	}

	@Override
	public void onMouseEnter(MouseEvent event) {
		if(loadState.get().loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onMouseEnter(isDragging);
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onMouseEnter(isDragging);
			}
		}
	}

	@Override
	public void onMouseExit(MouseEvent event) {
		if(loadState.get().loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onMouseExit(isDragging);
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onMouseExit(isDragging);
			}
		}
	}

}
