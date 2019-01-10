package sgora.mesh.editor.model.domain;

import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import sgora.mesh.editor.model.data.Point;
import sgora.mesh.editor.model.data.Rectangle;

import java.util.function.UnaryOperator;

public class ImageBox {

	private final Rectangle imageBoxModel;

	private Image baseImage;

	private Point mousePos = new Point();
	private Point lastMouseDragPoint;
	private Point canvasSize, baseImageSize;

	private static final double DEF_BORDER = 0.1;
	private static final double ZOOM_SPEED = 0.0025;
	private static final double DRAG_SPEED = 1;
	private static final double MIN_ZOOM = 0.2;
	private static final double MAX_ZOOM = 100;

	public ImageBox(Rectangle imageBoxModel) {
		this.imageBoxModel = imageBoxModel;
	}

	public void setBaseImage(String imagePath, Point canvasSize) {
		this.baseImage = new Image("file:" + imagePath);
		this.baseImageSize = new Point(baseImage.getWidth(), baseImage.getHeight());
		this.canvasSize = canvasSize;
		calcImageBox();
	}

	public void onScroll(ScrollEvent event) {
		double zoomAmount = -event.getDeltaY() * ZOOM_SPEED;
		Point zoomPos = new Point(mousePos).subtract(imageBoxModel.getPosition()).multiplyByScalar(zoomAmount);
		zoomAmount = 1 - zoomAmount;

		Point newImgSize = new Point(imageBoxModel.getSize()).multiplyByScalar(zoomAmount);
		if(newImgSize.x < canvasSize.x * MIN_ZOOM || newImgSize.y < canvasSize.y * MIN_ZOOM
				|| newImgSize.x > canvasSize.x * MAX_ZOOM || newImgSize.y > canvasSize.y * MAX_ZOOM)
			return;
		modifyPosition(pos -> pos.add(zoomPos));
		modifySize(size -> newImgSize);
		imageBoxModel.notifyListeners();
	}

	public void onDragStarted(MouseEvent event) {
		lastMouseDragPoint = new Point(event.getX(), event.getY());
	}

	public void onMouseDrag(MouseEvent event) {
		if(event.getButton() != MouseButton.MIDDLE)
			return;
		Point mousePos = new Point(event.getX(), event.getY());
		Point moveAmount = new Point(mousePos).subtract(lastMouseDragPoint).multiplyByScalar(DRAG_SPEED);

		modifyPosition(pos -> pos.add(moveAmount).clamp(new Point(imageBoxModel.getSize()).multiplyByScalar(-1), canvasSize));
		lastMouseDragPoint = mousePos;
		imageBoxModel.notifyListeners();
	}

	public void onMouseMove(MouseEvent event) {
		mousePos.x = event.getX();
		mousePos.y = event.getY();
	}

	public void onResizeCanvas(Point canvasSize) {
		if(baseImage == null)
			return;
		Point sizeRatio = new Point(canvasSize).divide(this.canvasSize);

		modifyPosition(pos -> pos.multiply(sizeRatio));
		modifySize(size -> size.multiplyByScalar((sizeRatio.y + sizeRatio.x) / 2));
		this.canvasSize = canvasSize;
		imageBoxModel.notifyListeners();
	}

	private void calcImageBox() {
		double imgRatio = baseImageSize.x / baseImageSize.y;

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
		imageBoxModel.notifyListeners();
	}

	private void modifyPosition(UnaryOperator<Point> operation) {
		imageBoxModel.setPosition(operation.apply(imageBoxModel.getPosition()));
	}

	private void modifySize(UnaryOperator<Point> operation) {
		imageBoxModel.setSize(operation.apply(imageBoxModel.getSize()));
	}

	public Rectangle getImageBoxModel() {
		return imageBoxModel;
	}

	public Image getBaseImage() {
		return baseImage;
	}

}
