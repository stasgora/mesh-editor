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

	private Point mousePos = new Point();
	private Point lastMouseDragPoint, lastCanvasSize;

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
		calcImageBox();
	}

	public void onScroll(ScrollEvent event) {
		double zoomAmount = -event.getDeltaY() * model().zoomSpeed;
		Point zoomPos = new Point(mousePos).subtract(model().imageBox.getPosition()).multiplyByScalar(zoomAmount);
		zoomAmount = 1 - zoomAmount;

		Point newImgSize = new Point(model().imageBox.getSize()).multiplyByScalar(zoomAmount);
		Point canvasSize = model.mainViewSize;
		if(newImgSize.x < canvasSize.x * MIN_ZOOM || newImgSize.y < canvasSize.y * MIN_ZOOM
				|| newImgSize.x > canvasSize.x * MAX_ZOOM || newImgSize.y > canvasSize.y * MAX_ZOOM)
			return;
		modifyPosition(pos -> pos.add(zoomPos));
		modifySize(size -> newImgSize);
		model().imageBox.notifyListeners();
	}

	public void onDragStart(MouseEvent event) {
		if(model().baseImage == null || model.activeTool.getValue() != MouseTool.IMAGE_MOVER || event.getButton() != model().dragButton)
			return;
		model().dragging = true;
		lastMouseDragPoint = new Point(event.getX(), event.getY());

		model.mouseCursor.setValue(Cursor.CLOSED_HAND);
	}

	public void onMouseDrag(MouseEvent event) {
		if(!model().dragging || event.getButton() != model().dragButton)
			return;
		Point mousePos = new Point(event.getX(), event.getY());
		Point moveAmount = new Point(mousePos).subtract(lastMouseDragPoint).multiplyByScalar(model().dragSpeed);

		modifyPosition(pos -> pos.add(moveAmount).clamp(new Point(model().imageBox.getSize()).multiplyByScalar(-1), model.mainViewSize));
		lastMouseDragPoint.set(mousePos);
		model().imageBox.notifyListeners();
	}

	public void onMouseMove(MouseEvent event) {
		mousePos.x = event.getX();
		mousePos.y = event.getY();
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

	public void onMouseEnter() {
		if(isDragging())
			model.mouseCursor.setValue(Cursor.HAND);
	}

	public void onMouseExit() {
		if(isDragging())
			model.mouseCursor.setValue(Cursor.DEFAULT);
	}

	private boolean isDragging() {
		return model().baseImage != null && model.activeTool.getValue() == MouseTool.IMAGE_MOVER && !model().dragging;
	}

	public void onDragEnd(MouseEvent event) {
		if(!model().dragging)
			return;
		model.mouseCursor.setValue(new Point(event.getX(), event.getY()).isBetween(new Point(), model.mainViewSize) ? Cursor.HAND : Cursor.DEFAULT);
		model().dragging = false;
	}
}
