package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
		model.mainViewSize.addListener(observable -> drawBothLayers());
		model.imageBoxModel.imageBox.addListener(observable -> drawBothLayers());
		model.meshBoxModel.mesh.addListener(observable -> drawMesh());
	}

	private void setEventHandlers() {
		setOnScroll(event -> imageBox.onScroll(event));
		setOnMouseMoved(event -> imageBox.onMouseMove(event));

		setOnMousePressed(event -> imageBox.onDragStart(event));
		setOnMouseDragged(event -> imageBox.onMouseDrag(event));

		setOnMouseReleased(this::onMouseRelease);
		setOnMouseEntered(this::onMouseEnter);
		setOnMouseExited(this::onMouseExit);
	}

	private void paneSizeChanged(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
		model.mainViewSize.set(new Point(getWidth(), getHeight()));
		model.mainViewSize.notifyListeners();
	}

	private void drawMesh() {
		meshCanvas.draw(model.meshBoxModel, meshBox.getMeshNodes());
	}

	private void drawImage() {
		imageCanvas.draw(model.imageBoxModel);
	}

	private void drawBothLayers() {
		drawImage();
		drawMesh();
	}

	private void onMouseEnter(MouseEvent event) {
		imageBox.onMouseEnter();
		meshBox.onMouseEnter();
	}

	private void onMouseExit(MouseEvent event) {
		imageBox.onMouseExit();
		meshBox.onMouseExit();
	}

	private void onMouseRelease(MouseEvent event) {
		imageBox.onDragEnd(event);
		meshBox.onMouseClick(event);
	}
}
