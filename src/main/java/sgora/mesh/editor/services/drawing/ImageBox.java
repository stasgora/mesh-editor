package sgora.mesh.editor.services.drawing;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.model.ImageBoxModel;
import sgora.mesh.editor.model.project.ProjectState;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.model.observables.SettableProperty;

public class ImageBox implements MouseListener {

	private Point lastCanvasSize;
	private double zoom = 1;

	private final Point mainViewSize;
	private final ProjectState projectState;
	private AppConfigReader appConfig;
	private AppConfigReader appSettings;
	private ObjectProperty<Cursor> mouseCursor;
	private ImageBoxModel imageBoxModel;

	public ImageBox(Point mainViewSize, ProjectState projectState, AppConfigReader appConfig, AppConfigReader appSettings, ObjectProperty<Cursor> mouseCursor, ImageBoxModel imageBoxModel) {
		this.mainViewSize = mainViewSize;
		this.projectState = projectState;
		this.appConfig = appConfig;
		this.appSettings = appSettings;
		this.mouseCursor = mouseCursor;
		this.imageBoxModel = imageBoxModel;
	}

	public void onResizeCanvas() {
		if(!projectState.loaded.get()) {
			return;
		}
		if(lastCanvasSize == null) {
			lastCanvasSize = new Point(mainViewSize);
			return;
		}
		Point sizeDiff = new Point(mainViewSize).subtract(lastCanvasSize);
		projectState.imageBox.position.add(sizeDiff.divideByScalar(2));
		lastCanvasSize.set(mainViewSize);
	}

	public void calcImageBox() {
		if(projectState.baseImage.get() == null) {
			return;
		}
		SettableProperty<Image> baseImage = projectState.baseImage;
		double imgRatio = baseImage.get().getWidth() / baseImage.get().getHeight();

		Point canvasSize = mainViewSize;
		double defBorder = appConfig.getDouble("imageBox.defaultBorder");
		if(imgRatio > canvasSize.x / canvasSize.y) {
			double imgWidth = canvasSize.x * (1 - defBorder);
			double imgHeight = imgWidth / imgRatio;
			projectState.imageBox.position.set(canvasSize.x * defBorder * 0.5, (canvasSize.y - imgHeight) / 2);
			projectState.imageBox.size.set(imgWidth, imgHeight);
		} else {
			double imgHeight = canvasSize.y * (1 - defBorder);
			double imgWidth = imgRatio * imgHeight;
			projectState.imageBox.position.set((canvasSize.x - imgWidth) / 2, canvasSize.y * defBorder * 0.5);
			projectState.imageBox.size.set(imgWidth, imgHeight);
		}
		projectState.imageBox.notifyListeners();
	}

	@Override
	public void onZoom(double amount, Point mousePos) {
		double minZoom = appConfig.getDouble("imageBox.zoom.min");
		double maxZoom = appConfig.getDouble("imageBox.zoom.max");

		Point baseImageSize = new Point(projectState.baseImage.get().getWidth(), projectState.baseImage.get().getHeight());
		double zoomFactor = 1 - amount * appSettings.getInt("settings.imageBox.zoom.dir") * appSettings.getDouble("settings.imageBox.zoom.speed");
		zoom = Math.max(minZoom, Math.min(maxZoom, zoom * zoomFactor));

		double moveFactor = 1 - baseImageSize.x * zoom / projectState.imageBox.size.x;
		Point zoomPos = new Point(mousePos).subtract(projectState.imageBox.position).multiplyByScalar(moveFactor);

		projectState.imageBox.position.add(zoomPos);
		projectState.imageBox.size.set(new Point(baseImageSize).multiplyByScalar(zoom));
		projectState.imageBox.notifyListeners();
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton button) {
		if(button == imageBoxModel.dragButton) {
			mouseCursor.setValue(Cursor.CLOSED_HAND);
		}
	}

	@Override
	public void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		if(button != imageBoxModel.dragButton) {
			return;
		}
		projectState.imageBox.position.add(dragAmount).clamp(new Point(projectState.imageBox.size).multiplyByScalar(-1), mainViewSize);
		projectState.imageBox.notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton button) {
		mouseCursor.setValue(mousePos.isBetween(new Point(), mainViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

	@Override
	public void onMouseEnter(boolean isDragging) {
		if(!isDragging) {
			mouseCursor.setValue(Cursor.HAND);
		}
	}

	@Override
	public void onMouseExit(boolean isDragging) {
		if(!isDragging) {
			mouseCursor.setValue(Cursor.DEFAULT);
		}
	}

}
