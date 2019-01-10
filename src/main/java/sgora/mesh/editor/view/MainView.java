package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.model.data.Point;
import sgora.mesh.editor.model.data.Rectangle;
import sgora.mesh.editor.model.domain.ImageBox;
import sgora.mesh.editor.model.domain.MeshBox;
import sgora.mesh.editor.ui.ImageCanvas;
import sgora.mesh.editor.ui.MeshCanvas;

import java.io.File;

public class MainView {

	private Stage stage;

	private ImageBox imageBox;
	private MeshBox meshBox;
	private final Rectangle imageBoxModel = new Rectangle();

	private final Point canvasViewSize = new Point();

	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;

	public AnchorPane canvasPane;

	public void init(Stage stage) {
		this.stage = stage;
		imageBox = new ImageBox(imageBoxModel);
		meshBox = new MeshBox(imageBoxModel);

		setEventHandlers();
		setListeners();
	}

	private void setListeners() {
		canvasPane.widthProperty().addListener(this::paneSizeChanged);
		canvasPane.heightProperty().addListener(this::paneSizeChanged);

		canvasViewSize.addListener(newVal -> {
			Point value = (Point) newVal;
			imageCanvas.setWidth(value.x);
			imageCanvas.setHeight(value.y);
			meshCanvas.setWidth(value.x);
			meshCanvas.setHeight(value.y);
		});
		canvasViewSize.addListener(newVal -> imageBox.onResizeCanvas(new Point((Point) newVal)));
		imageBox.getImageBoxModel().addListener(newVal -> imageCanvas.drawImage(imageBox.getBaseImage(), (Rectangle) newVal));
		imageBox.getImageBoxModel().addListener(newVal -> meshCanvas.drawMesh(meshBox.getMesh().getNodes()));
		meshBox.getMesh().addListener(newVal -> meshCanvas.drawMesh(meshBox.getMesh().getNodes()));
	}

	private void setEventHandlers() {
		meshCanvas.setOnScroll(event -> imageBox.onScroll(event));
		meshCanvas.setOnMouseMoved(event -> imageBox.onMouseMove(event));
		meshCanvas.setOnMouseDragged(event -> imageBox.onMouseDrag(event));
		meshCanvas.setOnMousePressed(event -> imageBox.onDragStarted(event));

		meshCanvas.setOnMouseReleased(event -> meshBox.onMouseClick(event));
	}

	public void loadImage(ActionEvent event) {
		FileChooser imageChooser = new FileChooser();
		imageChooser.setTitle("Choose Image");
		imageChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
		File image = imageChooser.showOpenDialog(stage);

		if(image == null)
			return;
		imageBox.setBaseImage(image.getAbsolutePath(), new Point(canvasViewSize));
	}

	public void exitApp(ActionEvent event) {
		Platform.exit();
	}

	private void paneSizeChanged(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
		canvasViewSize.set(new Point(canvasPane.getWidth(), canvasPane.getHeight()));
		canvasViewSize.notifyListeners();
	}
}
