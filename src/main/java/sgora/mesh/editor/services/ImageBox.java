package sgora.mesh.editor.services;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.model.containers.ImageBoxModel;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.containers.ProjectModel;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.interfaces.MouseListener;

public class ImageBox implements MouseListener {

	private final Model model;

	private Point lastCanvasSize;

	private static final double DEF_BORDER = 0.1;
	private static final double MIN_ZOOM = 0.2;
	private static final double MAX_ZOOM = 100;

	public ImageBox(Model model) {
		this.model = model;
	}
	
	private ImageBoxModel model() {
		return model.imageBoxModel;
	}

	private ProjectModel project() {
		return model.project;
	}

	public void onResizeCanvas() {
		if(!model.project.loaded.get())
			return;
		if(lastCanvasSize == null)
			lastCanvasSize = new Point(model.mainViewSize);
		Point sizeRatio = new Point(model.mainViewSize).divide(lastCanvasSize);
		lastCanvasSize.set(model.mainViewSize);
		model().imageBox.position.multiply(sizeRatio);
		model().imageBox.size.multiplyByScalar((sizeRatio.y + sizeRatio.x) / 2);
		model().imageBox.notifyListeners();
	}

	public void calcImageBox() {
		if(project().baseImage.get() == null)
			return;
		double imgRatio = project().baseImage.get().getWidth() / project().baseImage.get().getHeight();

		Point canvasSize = model.mainViewSize;
		if(imgRatio > canvasSize.x / canvasSize.y) {
			double imgWidth = canvasSize.x * (1 - DEF_BORDER);
			double imgHeight = imgWidth / imgRatio;
			model().imageBox.position.set(canvasSize.x * DEF_BORDER * 0.5, (canvasSize.y - imgHeight) / 2);
			model().imageBox.size.set(imgWidth, imgHeight);
		} else {
			double imgHeight = canvasSize.y * (1 - DEF_BORDER);
			double imgWidth = imgRatio * imgHeight;
			model().imageBox.position.set((canvasSize.x - imgWidth) / 2, canvasSize.y * DEF_BORDER * 0.5);
			model().imageBox.size.set(imgWidth, imgHeight);
		}
		model().imageBox.notifyListeners();
	}

	@Override
	public void onZoom(double amount, Point mousePos) {
		double zoomAmount = amount * model().zoomDir * model().zoomSpeed;
		Point zoomPos = new Point(mousePos).subtract(model().imageBox.position).multiplyByScalar(zoomAmount);
		Point newImgSize = new Point(model().imageBox.size).multiplyByScalar(1 - zoomAmount);

		if(newImgSize.x < model.mainViewSize.x * MIN_ZOOM || newImgSize.y < model.mainViewSize.y * MIN_ZOOM
				|| newImgSize.x > model.mainViewSize.x * MAX_ZOOM || newImgSize.y > model.mainViewSize.y * MAX_ZOOM)
			return;
		model().imageBox.position.add(zoomPos);
		model().imageBox.size.set(newImgSize);
		model().imageBox.notifyListeners();
	}

	@Override
	public void onDragStart(Point mousePos, MouseButton button) {
		if(button == model.imageBoxModel.dragButton)
			model.mouseCursor.setValue(Cursor.CLOSED_HAND);
	}

	@Override
	public void onMouseDrag(Point dragAmount, MouseButton button) {
		if(button != model.imageBoxModel.dragButton)
			return;
		model().imageBox.position.add(dragAmount.multiplyByScalar(model().dragSpeed)).clamp(new Point(model().imageBox.size).multiplyByScalar(-1), model.mainViewSize);
		model().imageBox.notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton button) {
		model.mouseCursor.setValue(mousePos.isBetween(new Point(), model.mainViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

	@Override
	public void onMouseEnter(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.HAND);
	}

	@Override
	public void onMouseExit(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.DEFAULT);
	}

}
