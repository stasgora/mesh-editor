package sgora.mesh.editor.services;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.model.containers.ImageBoxModel;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.interfaces.MouseListener;
import sgora.mesh.editor.model.observables.SettableProperty;

public class ImageBox implements MouseListener {

	private Point lastCanvasSize;

	private final Point mainViewSize;
	private final Project project;
	private AppConfigReader appConfig;
	private AppConfigReader appSettings;
	private ObjectProperty<Cursor> mouseCursor;
	private ImageBoxModel imageBoxModel;

	public ImageBox(Point mainViewSize, Project project, AppConfigReader appConfig, AppConfigReader appSettings, ObjectProperty<Cursor> mouseCursor, ImageBoxModel imageBoxModel) {
		this.mainViewSize = mainViewSize;
		this.project = project;
		this.appConfig = appConfig;
		this.appSettings = appSettings;
		this.mouseCursor = mouseCursor;
		this.imageBoxModel = imageBoxModel;
	}

	public void onResizeCanvas() {
		if(!project.loaded.get())
			return;
		if(lastCanvasSize == null)
			lastCanvasSize = new Point(mainViewSize);
		Point sizeRatio = new Point(mainViewSize).divide(lastCanvasSize);
		lastCanvasSize.set(mainViewSize);
		project.imageBox.position.multiply(sizeRatio);
		project.imageBox.size.multiplyByScalar((sizeRatio.y + sizeRatio.x) / 2);
		project.imageBox.notifyListeners();
	}

	public void calcImageBox() {
		if(project.baseImage.get() == null)
			return;
		SettableProperty<Image> baseImage = project.baseImage;
		double imgRatio = baseImage.get().getWidth() / baseImage.get().getHeight();

		Point canvasSize = mainViewSize;
		double defBorder = appConfig.getDouble("imageBox.defaultBorder");
		if(imgRatio > canvasSize.x / canvasSize.y) {
			double imgWidth = canvasSize.x * (1 - defBorder);
			double imgHeight = imgWidth / imgRatio;
			project.imageBox.position.set(canvasSize.x * defBorder * 0.5, (canvasSize.y - imgHeight) / 2);
			project.imageBox.size.set(imgWidth, imgHeight);
		} else {
			double imgHeight = canvasSize.y * (1 - defBorder);
			double imgWidth = imgRatio * imgHeight;
			project.imageBox.position.set((canvasSize.x - imgWidth) / 2, canvasSize.y * defBorder * 0.5);
			project.imageBox.size.set(imgWidth, imgHeight);
		}
		project.imageBox.notifyListeners();
	}

	@Override
	public void onZoom(double amount, Point mousePos) {
		double minZoom = appConfig.getDouble("imageBox.zoom.min");
		double maxZoom = appConfig.getDouble("imageBox.zoom.max");
		double zoomAmount = amount * appSettings.getInt("settings.imageBox.zoom.dir") * appSettings.getDouble("settings.imageBox.zoom.speed");

		Point newImgSize = new Point(project.imageBox.size).multiplyByScalar(1 - zoomAmount);
		newImgSize.clamp(new Point(mainViewSize).multiplyByScalar(minZoom), new Point(mainViewSize).multiplyByScalar(maxZoom));
		double correctedZoomAmount = 1 - new Point(newImgSize).divide(project.imageBox.size).x;

		Point zoomPos = new Point(mousePos).subtract(project.imageBox.position).multiplyByScalar(correctedZoomAmount);
		project.imageBox.position.add(zoomPos);
		project.imageBox.size.set(newImgSize);
		project.imageBox.notifyListeners();
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton button) {
		if(button == imageBoxModel.dragButton)
			mouseCursor.setValue(Cursor.CLOSED_HAND);
	}

	@Override
	public void onMouseDrag(Point dragAmount, MouseButton button) {
		if(button != imageBoxModel.dragButton)
			return;
		project.imageBox.position.add(dragAmount).clamp(new Point(project.imageBox.size).multiplyByScalar(-1), mainViewSize);
		project.imageBox.notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton button) {
		mouseCursor.setValue(mousePos.isBetween(new Point(), mainViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

	@Override
	public void onMouseEnter(boolean isDragging) {
		if(!isDragging)
			mouseCursor.setValue(Cursor.HAND);
	}

	@Override
	public void onMouseExit(boolean isDragging) {
		if(!isDragging)
			mouseCursor.setValue(Cursor.DEFAULT);
	}

}
