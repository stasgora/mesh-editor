package sgora.mesh.editor.services;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import sgora.mesh.editor.model.containers.ImageBoxModel;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;

import java.util.function.UnaryOperator;

public class ImageBox {

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

	public void setBaseImage(String imagePath) {
		model().baseImage = new Image("file:" + imagePath);
		model.imageBoxModel.imageLoaded = true;
		calcImageBox();
	}

	public void onResizeCanvas() {
		if(model().baseImage == null || model.mainViewSize.x == 0 || model.mainViewSize.y == 0)
			return;
		if(lastCanvasSize == null)
			lastCanvasSize = new Point(model.mainViewSize);
		Point sizeRatio = new Point(model.mainViewSize).divide(lastCanvasSize);
		lastCanvasSize.set(model.mainViewSize);
		modifyPosition(pos -> pos.multiply(sizeRatio));
		modifySize(size -> size.multiplyByScalar((sizeRatio.y + sizeRatio.x) / 2));
		model().imageBox.notifyListeners();
	}

	private void calcImageBox() {
		double imgRatio = model().baseImage.getWidth() / model().baseImage.getHeight();

		Point canvasSize = model.mainViewSize;
		if(imgRatio > canvasSize.x / canvasSize.y) {
			double imgWidth = canvasSize.x * (1 - DEF_BORDER);
			double imgHeight = imgWidth / imgRatio;
			modifyPosition(pos -> new Point(canvasSize.x * DEF_BORDER * 0.5, (canvasSize.y - imgHeight) / 2));
			modifySize(size -> new Point(imgWidth, imgHeight));
		} else {
			double imgHeight = canvasSize.y * (1 - DEF_BORDER);
			double imgWidth = imgRatio * imgHeight;
			modifyPosition(pos -> new Point((canvasSize.x - imgWidth) / 2, canvasSize.y * DEF_BORDER * 0.5));
			modifySize(size -> new Point(imgWidth, imgHeight));
		}
		model().imageBox.notifyListeners();
	}

	private void modifyPosition(UnaryOperator<Point> operation) {
		model().imageBox.setPosition(operation.apply(model().imageBox.getPosition()));
	}

	private void modifySize(UnaryOperator<Point> operation) {
		model().imageBox.setSize(operation.apply(model().imageBox.getSize()));
	}

	public void onZoom(double amount, Point mousePos) {
		double zoomAmount = amount * model().zoomDir * model().zoomSpeed;
		Point zoomPos = new Point(mousePos).subtract(model().imageBox.getPosition()).multiplyByScalar(zoomAmount);
		Point newImgSize = new Point(model().imageBox.getSize()).multiplyByScalar(1 - zoomAmount);

		if(newImgSize.x < model.mainViewSize.x * MIN_ZOOM || newImgSize.y < model.mainViewSize.y * MIN_ZOOM
				|| newImgSize.x > model.mainViewSize.x * MAX_ZOOM || newImgSize.y > model.mainViewSize.y * MAX_ZOOM)
			return;
		modifyPosition(pos -> pos.add(zoomPos));
		modifySize(size -> newImgSize);
		model().imageBox.notifyListeners();
	}

	public void onDragStart() {
		model.mouseCursor.setValue(Cursor.CLOSED_HAND);
	}

	public void onMouseDrag(Point dragAmount) {
		System.out.println(dragAmount);
		modifyPosition(pos -> pos.add(dragAmount.multiplyByScalar(model().dragSpeed)).clamp(new Point(model().imageBox.getSize()).multiplyByScalar(-1), model.mainViewSize));
		model().imageBox.notifyListeners();
	}

	public void onDragEnd(Point mousePos) {
		model.mouseCursor.setValue(mousePos.isBetween(new Point(), model.mainViewSize) ? Cursor.HAND : Cursor.DEFAULT);
	}

	public void onMouseEnter(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.HAND);
	}

	public void onMouseExit(boolean isDragging) {
		if(!isDragging)
			model.mouseCursor.setValue(Cursor.DEFAULT);
	}

}
