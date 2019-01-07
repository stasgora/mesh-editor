package sgora.mesh.editor.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.model.Point;
import sgora.mesh.editor.model.Rectangle;
import sgora.mesh.editor.view.ImageCanvas;
import sgora.mesh.editor.view.MeshCanvas;

import java.io.File;

public class AppController {

	private Stage stage;

	private ImageBoxController imageBoxController;
	private MeshController meshController;

	private final Point canvasViewSize = new Point();

	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;

	public AnchorPane canvasPane;
	public SplitPane splitPane;

	public void init(Stage stage) {
		this.stage = stage;
		imageBoxController = new ImageBoxController();
		meshController = new MeshController();

		canvasPane.widthProperty().addListener((observable, oldVal, newVal) -> {
			canvasViewSize.set(new Point(canvasPane.getWidth(), canvasPane.getHeight()));
			canvasViewSize.notifyListeners();
		});
		canvasPane.heightProperty().addListener((observable, oldVal, newVal) -> {
			canvasViewSize.set(new Point(canvasPane.getWidth(), canvasPane.getHeight()));
			canvasViewSize.notifyListeners();
		});

		canvasViewSize.addListener(newVal -> {
			Point value = (Point) newVal;
			imageCanvas.setWidth(value.x);
			imageCanvas.setHeight(value.y);
			meshCanvas.setWidth(value.x);
			meshCanvas.setHeight(value.y);
		});

		meshCanvas.setOnScroll(event -> imageBoxController.onScroll(event));
		meshCanvas.setOnMouseMoved(event -> imageBoxController.onMouseMove(event));
		meshCanvas.setOnMouseDragged(event -> imageBoxController.onMouseDrag(event));
		meshCanvas.setOnMousePressed(event -> imageBoxController.onDragStarted(event));
		meshCanvas.setOnMouseClicked(event -> meshController.onMouseClick(event));

		canvasViewSize.addListener(newVal -> imageBoxController.onResizeCanvas(new Point((Point) newVal)));
		imageBoxController.getImageBoxModel().addListener(newVal -> imageCanvas.drawImage(imageBoxController.getBaseImageModel(), (Rectangle) newVal));
		meshController.getMeshModel().addListener(newVal -> meshCanvas.drawMesh(meshController.getMeshModel().getNodes()));
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
