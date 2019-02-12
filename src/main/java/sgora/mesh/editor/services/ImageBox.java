package sgora.mesh.editor.services;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.State;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.observables.SettableProperty;

public class ImageBox implements MouseListener {

	private final State state;

	private Point lastCanvasSize;

	public ImageBox(State state) {
		this.state = state;
	}

	private Rectangle imageBox() {
		return state.model.imageBox;
	}

	public void onResizeCanvas() {
		if(!state.model.project.loaded.get())
			return;
		if(lastCanvasSize == null)
			lastCanvasSize = new Point(state.model.mainViewSize);
		Point sizeRatio = new Point(state.model.mainViewSize).divide(lastCanvasSize);
		lastCanvasSize.set(state.model.mainViewSize);
		imageBox().position.multiply(sizeRatio);
		imageBox().size.multiplyByScalar((sizeRatio.y + sizeRatio.x) / 2);
		imageBox().notifyListeners();
	}

	public void calcImageBox() {
		if(state.model.project.baseImage.get() == null)
			return;
		SettableProperty<Image> baseImage = state.model.project.baseImage;
		double imgRatio = baseImage.get().getWidth() / baseImage.get().getHeight();

		Point canvasSize = state.model.mainViewSize;
		double defBorder = state.config.appConfig.<Double>getValue("imageBox.defaultBorder");
		if(imgRatio > canvasSize.x / canvasSize.y) {
			double imgWidth = canvasSize.x * (1 - defBorder);
			double imgHeight = imgWidth / imgRatio;
			imageBox().position.set(canvasSize.x * defBorder * 0.5, (canvasSize.y - imgHeight) / 2);
			imageBox().size.set(imgWidth, imgHeight);
		} else {
			double imgHeight = canvasSize.y * (1 - defBorder);
			double imgWidth = imgRatio * imgHeight;
			imageBox().position.set((canvasSize.x - imgWidth) / 2, canvasSize.y * defBorder * 0.5);
			imageBox().size.set(imgWidth, imgHeight);
		}
		imageBox().notifyListeners();
	}

	@Override
	public void onZoom(double amount, Point mousePos) {
		double zoomAmount = amount * state.model.imageBoxModel.zoomDir * state.model.imageBoxModel.zoomSpeed;
		Point zoomPos = new Point(mousePos).subtract(imageBox().position).multiplyByScalar(zoomAmount);
		Point newImgSize = new Point(imageBox().size).multiplyByScalar(1 - zoomAmount);

		double minZoom = state.config.appConfig.getDouble("imageBox.zoom.min");
		double maxZoom = state.config.appConfig.getDouble("imageBox.zoom.max");
		if(newImgSize.x < state.model.mainViewSize.x * minZoom || newImgSize.y < state.model.mainViewSize.y * minZoom
				|| newImgSize.x > state.model.mainViewSize.x * maxZoom || newImgSize.y > state.model.mainViewSize.y * maxZoom)
			return;
		imageBox().position.add(zoomPos);
		imageBox().size.set(newImgSize);
		imageBox().notifyListeners();
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton button) {
		if(button == state.model.imageBoxModel.dragButton)
			state.model.mouseCursor.setValue(Cursor.CLOSED_HAND);
	}

	@Override
	public void onMouseDrag(Point dragAmount, MouseButton button) {
		if(button != state.model.imageBoxModel.dragButton)
			return;
		imageBox().position.add(dragAmount).clamp(new Point(imageBox().size).multiplyByScalar(-1), state.model.mainViewSize);
		imageBox().notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton button) {
		state.model.mouseCursor.setValue(mousePos.isBetween(new Point(), state.model.mainViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

	@Override
	public void onMouseEnter(boolean isDragging) {
		if(!isDragging)
			state.model.mouseCursor.setValue(Cursor.HAND);
	}

	@Override
	public void onMouseExit(boolean isDragging) {
		if(!isDragging)
			state.model.mouseCursor.setValue(Cursor.DEFAULT);
	}

}
