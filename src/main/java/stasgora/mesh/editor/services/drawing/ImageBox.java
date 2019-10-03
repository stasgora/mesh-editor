package stasgora.mesh.editor.services.drawing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.stasgora.observetree.SettableProperty;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.model.project.CanvasUI;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.config.annotation.AppSettings;
import stasgora.mesh.editor.services.config.interfaces.AppConfigManager;
import stasgora.mesh.editor.services.config.interfaces.AppConfigReader;
import stasgora.mesh.editor.services.input.MouseListener;

@Singleton
public class ImageBox implements MouseListener {

	private Point lastCanvasSize;
	private double zoom = 1;

	private final Point canvasViewSize;
	private final CanvasData canvasData;
	private final AppConfigReader appConfig;
	private final AppConfigReader appSettings;
	private CanvasUI canvasUI;

	@Inject
	ImageBox(CanvasData canvasData, CanvasUI canvasUI, @AppConfig AppConfigReader appConfig, @AppSettings AppConfigManager appSettings) {
		this.canvasData = canvasData;
		this.appConfig = appConfig;
		this.appSettings = appSettings;
		this.canvasUI = canvasUI;
		canvasViewSize = canvasUI.canvasViewSize;
	}

	public void onResizeCanvas() {
		if (canvasData.baseImage.get() != null) {
			return;
		}
		if (lastCanvasSize == null) {
			lastCanvasSize = new Point(canvasViewSize);
			return;
		}
		Point sizeDiff = new Point(canvasViewSize).subtract(lastCanvasSize);
		canvasData.imageBox.getPosition().add(sizeDiff.divideByScalar(2));
		lastCanvasSize.set(canvasViewSize);
	}

	public void calcImageBox() {
		if (canvasData.baseImage.get() == null) {
			return;
		}
		SettableProperty<Image> baseImage = canvasData.baseImage;
		double imgRatio = baseImage.get().getWidth() / baseImage.get().getHeight();

		Point canvasSize = canvasViewSize;
		double defBorder = appConfig.getDouble("imageBox.defaultBorder");
		if (imgRatio > canvasSize.getX() / canvasSize.getY()) {
			double imgWidth = canvasSize.getX() * (1 - defBorder);
			double imgHeight = imgWidth / imgRatio;
			canvasData.imageBox.getPosition().set(canvasSize.getX() * defBorder * 0.5, (canvasSize.getY() - imgHeight) / 2);
			canvasData.imageBox.getSize().set(imgWidth, imgHeight);
		} else {
			double imgHeight = canvasSize.getY() * (1 - defBorder);
			double imgWidth = imgRatio * imgHeight;
			canvasData.imageBox.getPosition().set((canvasSize.getX() - imgWidth) / 2, canvasSize.getY() * defBorder * 0.5);
			canvasData.imageBox.getSize().set(imgWidth, imgHeight);
		}
		canvasData.imageBox.notifyListeners();
	}

	public boolean onZoom(double amount, Point mousePos) {
		double minZoom = appConfig.getDouble("imageBox.zoom.min");
		double maxZoom = appConfig.getDouble("imageBox.zoom.max");

		Point baseImageSize = new Point(canvasData.baseImage.get().getWidth(), canvasData.baseImage.get().getHeight());
		double zoomFactor = 1 - amount * appSettings.getInt("settings.imageBox.zoom.dir") * appSettings.getDouble("settings.imageBox.zoom.speed");
		zoom = Math.max(minZoom, Math.min(maxZoom, zoom * zoomFactor));

		double moveFactor = 1 - baseImageSize.getX() * zoom / canvasData.imageBox.getSize().getX();
		Point zoomPos = new Point(mousePos).subtract(canvasData.imageBox.getPosition()).multiplyByScalar(moveFactor);

		canvasData.imageBox.getPosition().add(zoomPos);
		canvasData.imageBox.getSize().set(new Point(baseImageSize).multiplyByScalar(zoom));
		canvasData.notifyListeners();
		return true;
	}

	@Override
	public boolean onDragStart(Point mousePos, MouseButton button) {
		if (button == canvasUI.mouseConfig.dragImageButton) {
			canvasUI.canvasMouseCursor.setValue(Cursor.CLOSED_HAND);
			return true;
		}
		return false;
	}

	@Override
	public void onMouseDrag(Point dragAmount, Point mousePos, MouseButton button) {
		if (button != canvasUI.mouseConfig.dragImageButton) {
			return;
		}
		Rectangle imageBox = this.canvasData.imageBox;
		imageBox.getPosition().add(dragAmount).clamp(new Point(imageBox.getSize()).multiplyByScalar(-1), canvasViewSize);
		imageBox.notifyListeners();
	}

	@Override
	public void onDragEnd(Point mousePos, MouseButton button) {
		canvasUI.canvasMouseCursor.setValue(mousePos.isBetween(new Point(), canvasViewSize) ? canvasUI.mouseConfig.defaultCanvasCursor : Cursor.DEFAULT);
	}

}
