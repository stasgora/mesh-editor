package stasgora.mesh.editor.services.input;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import stasgora.mesh.editor.model.MouseConfig;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.project.CanvasUI;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.drawing.ImageBox;
import stasgora.mesh.editor.services.drawing.MeshBox;

@Singleton
class CanvasActionFacade implements CanvasAction {

	private final MouseListener[] eventConsumersQueue;
	private final LoadState loadState;
	private final CanvasUI canvasUI;
	private final ImageBox imageBox;
	private final MeshBox meshBox;

	private Point lastMouseDragPoint;
	private MouseListener activeConsumer;

	@Inject
	CanvasActionFacade(LoadState loadState, ImageBox imageBox, MeshBox meshBox, CanvasUI canvasUI) {
		this.imageBox = imageBox;
		this.meshBox = meshBox;
		this.loadState = loadState;
		this.canvasUI = canvasUI;
		eventConsumersQueue = new MouseListener[]{meshBox, imageBox};
	}

	@Override
	public void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if (loadState.loaded.get()) {
			for (MouseListener consumer : eventConsumersQueue) {
				if (consumer.onDragStart(mousePos, event.getButton())) {
					activeConsumer = consumer;
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
		if (loadState.loaded.get() && activeConsumer != null) {
			activeConsumer.onMouseDrag(new Point(dragAmount), mousePos, event.getButton());
		}
		lastMouseDragPoint.set(mousePos);
	}

	@Override
	public void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if (loadState.loaded.get() && activeConsumer != null) {
			activeConsumer.onDragEnd(new Point(mousePos), event.getButton());
		}
	}

	@Override
	public void onScroll(ScrollEvent event) {
		if (loadState.loaded.get()) {
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
		}
	}

	@Override
	public void onMouseEnter(MouseEvent event) {
		if (loadState.loaded.get() && lastMouseDragPoint == null) {
			canvasUI.canvasMouseCursor.setValue(canvasUI.mouseConfig.defaultCanvasCursor);
		}
	}

	@Override
	public void onMouseExit(MouseEvent event) {
		if (loadState.loaded.get() && lastMouseDragPoint == null) {
			canvasUI.canvasMouseCursor.setValue(Cursor.DEFAULT);
		}
	}

	@Override
	public void onMouseMove(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if (loadState.loaded.get()) {
			meshBox.onMouseMove(mousePos);
		}
	}

}
