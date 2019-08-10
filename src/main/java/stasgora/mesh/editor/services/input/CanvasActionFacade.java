package stasgora.mesh.editor.services.input;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import stasgora.mesh.editor.model.MouseConfig;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.drawing.ImageBox;
import stasgora.mesh.editor.services.drawing.MeshBox;

public class CanvasActionFacade implements CanvasAction {

	private MouseListener[] eventConsumersQueue;
	private final LoadState loadState;
	private MouseConfig mouseConfig;

	private Point lastMouseDragPoint;
	private ImageBox imageBox;
	private MouseListener activeConsumer;
	private MeshBox meshBox;
	private ObjectProperty<Cursor> mouseCursor;

	public CanvasActionFacade(LoadState loadState, ImageBox imageBox, MeshBox meshBox, ObjectProperty<Cursor> mouseCursor, MouseConfig mouseConfig) {
		this.imageBox = imageBox;
		this.meshBox = meshBox;
		this.mouseCursor = mouseCursor;
		this.loadState = loadState;
		this.mouseConfig = mouseConfig;
		eventConsumersQueue = new MouseListener[] {meshBox, imageBox};
	}

	@Override
	public void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.loaded.get()) {
			for (MouseListener consumer : eventConsumersQueue) {
				if(consumer.onDragStart(mousePos, event.getButton())) {
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
		if(loadState.loaded.get() && activeConsumer != null) {
			activeConsumer.onMouseDrag(new Point(dragAmount), mousePos, event.getButton());
		}
		lastMouseDragPoint.set(mousePos);
	}

	@Override
	public void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.loaded.get() && activeConsumer != null) {
			activeConsumer.onDragEnd(new Point(mousePos), event.getButton());
		}
	}

	@Override
	public void onScroll(ScrollEvent event) {
		if(loadState.loaded.get()) {
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
		}
	}

	@Override
	public void onMouseEnter(MouseEvent event) {
		if(loadState.loaded.get() && lastMouseDragPoint == null) {
			mouseCursor.setValue(mouseConfig.defaultCanvasCursor);
		}
	}

	@Override
	public void onMouseExit(MouseEvent event) {
		if(loadState.loaded.get() && lastMouseDragPoint == null) {
			mouseCursor.setValue(Cursor.DEFAULT);
		}
	}

	@Override
	public void onMouseMove(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.loaded.get()) {
			meshBox.onMouseMove(mousePos);
		}
	}

}
