package sgora.mesh.editor.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.model.Point;
import sgora.mesh.editor.model.Rectangle;
import sgora.mesh.editor.view.CanvasView;

import java.io.File;

public class AppController {

	private Stage stage;

	private ImageBoxController imageBoxController;

	private final Point canvasViewSize = new Point();

	public CanvasView canvasView;
	public AnchorPane canvasPane;
	public SplitPane splitPane;

	public void init(Stage stage) {
		this.stage = stage;
		imageBoxController = new ImageBoxController();

		canvasPane.widthProperty().addListener((observable, oldVal, newVal) -> {
			canvasViewSize.set(new Point(canvasPane.getWidth(), canvasPane.getHeight()));
			canvasViewSize.notifyListeners();
		});
		canvasPane.heightProperty().addListener((observable, oldVal, newVal) -> {
			canvasViewSize.set(new Point(canvasPane.getWidth(), canvasPane.getHeight()));
			canvasViewSize.notifyListeners();
		});

		canvasViewSize.addSetListener(newVal -> {
			Point value = (Point) newVal;
			canvasView.setWidth(value.x);
			canvasView.setHeight(value.y);
		});

		canvasView.setOnScroll(event -> imageBoxController.onScroll(event));
		canvasView.setOnMouseMoved(event -> imageBoxController.onMouseMove(event));
		canvasView.setOnMouseDragged(event -> imageBoxController.onMouseDrag(event));
		canvasView.setOnMousePressed(event -> imageBoxController.onDragStarted(event));

		canvasViewSize.addSetListener(newVal -> imageBoxController.onResizeCanvas(new Point((Point) newVal)));
		imageBoxController.getImageBoxModel().addSetListener(newVal -> canvasView.draw((Rectangle) newVal, imageBoxController.getBaseImageModel()));
	}

	public void loadImage(ActionEvent event) {
		FileChooser imageChooser = new FileChooser();
		imageChooser.setTitle("Choose Image");
		imageChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
		File image = imageChooser.showOpenDialog(stage);

		if(image == null)
			return;
		imageBoxController.setBaseImage(image.getAbsolutePath(), new Point(canvasViewSize));
	}

	public void exitApp(ActionEvent event) {
		Platform.exit();
	}

}
