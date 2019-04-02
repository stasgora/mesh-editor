package sgora.mesh.editor.services.drawing;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.interfaces.CanvasAction;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.project.LoadState;

public class CanvasActionFacade implements CanvasAction {

	private MouseListener[] eventConsumers;
	private final LoadState loadState;
	private final SettableProperty<MouseTool> activeTool;

	private Point lastMouseDragPoint;

	public CanvasActionFacade(LoadState loadState, ImageBox imageBox, MeshBox meshBox, SettableProperty<MouseTool> activeTool) {
		eventConsumers = new MouseListener[] {meshBox, imageBox};
		this.loadState = loadState;
		this.activeTool = activeTool;
	}

	@Override
	public void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.loaded.get()) {
			for (MouseListener consumer : eventConsumers) {
				if(consumer.onDragStart(mousePos, event.getButton())) {
					break;
				}
			}
		}
		lastMouseDragPoint = mousePos;
	}

	@Override
	public void onMouseDrag(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		Point dragAmount = new Point(mousePos).subtract(lastMouseDragPoint);
		if(loadState.loaded.get()) {
			for (MouseListener consumer : eventConsumers) {
				if(consumer.onMouseDrag(new Point(dragAmount), mousePos, event.getButton())) {
					break;
				}
			}
		}
		lastMouseDragPoint.set(mousePos);
	}

	@Override
	public void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.loaded.get()) {
			for (MouseListener consumer : eventConsumers) {
				if(consumer.onDragEnd(new Point(mousePos), event.getButton())) {
					break;
				}
			}
		}
	}

	@Override
	public void onScroll(ScrollEvent event) {
		if(loadState.loaded.get()) {
			for (MouseListener consumer : eventConsumers) {
				if(consumer.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()))) {
					break;
				}
			}
		}
	}

	@Override
	public void onMouseEnter(MouseEvent event) {
		if(loadState.loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			for (MouseListener consumer : eventConsumers) {
				if(consumer.onMouseEnter(isDragging)) {
					break;
				}
			}
		}
	}

	@Override
	public void onMouseExit(MouseEvent event) {
		if(loadState.loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			for (MouseListener consumer : eventConsumers) {
				if(consumer.onMouseExit(isDragging)) {
					break;
				}
			}
		}
	}

}
