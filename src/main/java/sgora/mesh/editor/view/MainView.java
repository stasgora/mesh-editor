package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.AnchorPane;
import sgora.mesh.editor.model.ObservableModel;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.services.ImageBox;
import sgora.mesh.editor.services.MeshBox;
import sgora.mesh.editor.ui.ImageCanvas;
import sgora.mesh.editor.ui.MeshCanvas;

public class MainView extends AnchorPane {

	private ImageCanvas imageCanvas;
	private MeshCanvas meshCanvas;

	public ImageBox imageBox;
	public MeshBox meshBox;

	private Model model;

	public void init(Model model, ImageCanvas imageCanvas, MeshCanvas meshCanvas) {
		this.model = model;
		this.imageCanvas = imageCanvas;
		this.meshCanvas = meshCanvas;
		
		imageBox = new ImageBox(model);
		meshBox = new MeshBox(model);

		setListeners();
		setEventHandlers();
	}

	private void setListeners() {
		widthProperty().addListener(this::paneSizeChanged);
		heightProperty().addListener(this::paneSizeChanged);

		model.mainViewSize.addListener(newVal -> {
			Point value = (Point) newVal;
			imageCanvas.setWidth(value.x);
			imageCanvas.setHeight(value.y);
			meshCanvas.setWidth(value.x);
			meshCanvas.setHeight(value.y);
		});
		model.mainViewSize.addListener(newVal -> imageBox.onResizeCanvas());
		model.mainViewSize.addListener(this::drawImage);
		model.mainViewSize.addListener(this::drawMesh);
		model.imageBoxModel.imageBox.addListener(this::drawImage);
		model.imageBoxModel.imageBox.addListener(this::drawMesh);
		model.meshBoxModel.mesh.addListener(this::drawMesh);
	}

	private void setEventHandlers() {
		meshCanvas.setOnScroll(event -> imageBox.onScroll(event));
		meshCanvas.setOnMouseMoved(event -> imageBox.onMouseMove(event));
		meshCanvas.setOnMouseDragged(event -> imageBox.onMouseDrag(event));
		meshCanvas.setOnMousePressed(event -> imageBox.onDragStarted(event));

		meshCanvas.setOnMouseReleased(event -> meshBox.onMouseClick(event));
	}

	private void paneSizeChanged(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
		model.mainViewSize.set(new Point(getWidth(), getHeight()));
		model.mainViewSize.notifyListeners();
	}

	private void drawMesh(ObservableModel newVal) {
		meshCanvas.draw(model.meshBoxModel, meshBox.getMeshNodes());
	}

	private void drawImage(ObservableModel newVal) {
		imageCanvas.draw(model.imageBoxModel);
	}
}
