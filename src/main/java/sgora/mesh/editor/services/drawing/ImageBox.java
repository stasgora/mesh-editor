package sgora.mesh.editor.services.drawing;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.model.KeysConfig;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.model.observables.SettableProperty;

public class ImageBox implements MouseListener {

	private Point lastCanvasSize;
	private double zoom = 1;

	private final Point canvasViewSize;
	private final CanvasData canvasData;
	private AppConfigReader appConfig;
	private AppConfigReader appSettings;
	private ObjectProperty<Cursor> mouseCursor;
	private KeysConfig keysConfig;

	public ImageBox(Point canvasViewSize, CanvasData canvasData, AppConfigReader appConfig,
	                AppConfigReader appSettings, ObjectProperty<Cursor> mouseCursor, KeysConfig keysConfig) {
		this.canvasViewSize = canvasViewSize;
		this.canvasData = canvasData;
		this.appConfig = appConfig;
		this.appSettings = appSettings;
		this.mouseCursor = mouseCursor;
		this.keysConfig = keysConfig;
	}

	public void onResizeCanvas() {
		if(canvasData.baseImage.get() != null) {
			return;
		}
		if(lastCanvasSize == null) {
			lastCanvasSize = new Point(canvasViewSize);
			return;
		}
		Point sizeDiff = new Point(canvasViewSize).subtract(lastCanvasSize);
		canvasData.imageBox.position.add(sizeDiff.divideByScalar(2));
		lastCanvasSize.set(canvasViewSize);
	}

	public void calcImageBox() {
		if(canvasData.baseImage.get() == null) {
			return;
		}
		SettableProperty<Image> baseImage = canvasData.baseImage;
		double imgRatio = baseImage.get().getWidth() / baseImage.get().getHeight();

		Point canvasSize = canvasViewSize;
		double defBorder = appConfig.getDouble("imageBox.defaultBorder");
		if(imgRatio > canvasSize.x / canvasSize.y) {
			double imgWidth = canvasSize.x * (1 - defBorder);
			double imgHeight = imgWidth / imgRatio;
			canvasData.imageBox.position.set(canvasSize.x * defBorder * 0.5, (canvasSize.y - imgHeight) / 2);
			canvasData.imageBox.size.set(imgWidth, imgHeight);
		} else {
			double imgHeight = canvasSize.y * (1 - defBorder);
			double imgWidth = imgRatio * imgHeight;
			canvasData.imageBox.position.set((canvasSize.x - imgWidth) / 2, canvasSize.y * defBorder * 0.5);
			canvasData.imageBox.size.set(imgWidth, imgHeight);
		}
		canvasData.imageBox.notifyListeners();
	}

	@Override
	public boolean onZoom(double amount, Point mousePos) {
		double minZoom = appConfig.getDouble("imageBox.zoom.min");
		double maxZoom = appConfig.getDouble("imageBox.zoom.max");

		Point baseImageSize = new Point(canvasData.baseImage.get().getWidth(), canvasData.baseImage.get().getHeight());
		double zoomFactor = 1 - amount * appSettings.getInt("settings.imageBox.zoom.dir") * appSettings.getDouble("settings.imageBox.zoom.speed");
		zoom = Math.max(minZoom, Math.min(maxZoom, zoom * zoomFactor));

		double moveFactor = 1 - baseImageSize.x * zoom / canvasData.imageBox.size.x;
		Point zoomPos = new Point(mousePos).subtract(canvasData.imageBox.position).multiplyByScalar(moveFactor);

		canvasData.imageBox.position.add(zoomPos);
		canvasData.imageBox.size.set(new Point(baseImageSize).multiplyByScalar(zoom));
		canvasData.notifyListeners();
		return true;
	}

	@Override
	public boolean onDragStart(Point mousePos, MouseButton button) {
		if(button == keysConfig.dragImageButton) {
			mouseCursor.setValue(Cursor.CLOSED_HAND);
			return true;
		}
		return false;
	}

	@Override
	public boolean onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		if(button != keysConfig.dragImageButton) {
			return false;
		}
		Rectangle imageBox = this.canvasData.imageBox;
		imageBox.position.add(dragAmount).clamp(new Point(imageBox.size).multiplyByScalar(-1), canvasViewSize);
		imageBox.notifyListeners();
		return true;
	}

	@Override
	public boolean onDragEnd(Point mousePos, MouseButton button) {
		mouseCursor.setValue(mousePos.isBetween(new Point(), canvasViewSize) ? Cursor.HAND : Cursor.DEFAULT);
		return true;
	}

	@Override
	public boolean onMouseEnter(boolean isDragging) {
		if(!isDragging) {
			mouseCursor.setValue(Cursor.HAND);
		}
		return true;
	}

}
