package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;
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

	private Point lastMouseDragPoint;

	public void init(Model model, ImageCanvas imageCanvas, MeshCanvas meshCanvas) {
		this.model = model;
		this.imageCanvas = imageCanvas;
		this.meshCanvas = meshCanvas;
		
		imageBox = new ImageBox(model);
		meshBox = new MeshBox(model);

		setListeners();
		setMouseHandlers();
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

	private void setMouseHandlers() {
		setOnScroll(this::onScroll);
		setOnMouseMoved(this::onMouseMove);

		setOnMousePressed(this::onMousePress);
		setOnMouseDragged(this::onMouseDrag);

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

	private void onMousePress(MouseEvent event) {
		lastMouseDragPoint = new Point(event.getX(), event.getY());
		if(model.imageBoxModel.imageLoaded) {
			if(model.activeTool.getValue() == MouseTool.IMAGE_MOVER && event.getButton() == model.imageBoxModel.dragButton)
				imageBox.onDragStart();

		}
	}

	private void onMouseDrag(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		Point dragAmount = new Point(mousePos).subtract(lastMouseDragPoint);
		if(model.imageBoxModel.imageLoaded) {
			if (model.activeTool.getValue() == MouseTool.IMAGE_MOVER && event.getButton() == model.imageBoxModel.dragButton)
				imageBox.onMouseDrag(new Point(dragAmount));
		}
		lastMouseDragPoint.set(mousePos);
	}

	private void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(model.imageBoxModel.imageLoaded) {
			if(model.activeTool.getValue() == MouseTool.IMAGE_MOVER)
				imageBox.onDragEnd(new Point(mousePos));
			else if(model.activeTool.getValue() == MouseTool.MESH_EDITOR)
				meshBox.onMouseClick(new Point(mousePos), event.getButton());
		}
	}

	private void onScroll(ScrollEvent event) {
		if(model.imageBoxModel.imageLoaded)
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
	}

	private void onMouseMove(MouseEvent event) {
	}

	private void onMouseEnter(MouseEvent event) {
		if(model.imageBoxModel.imageLoaded) {
			if(model.activeTool.getValue() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseEnter(lastMouseDragPoint != null);
			else if(model.activeTool.getValue() == MouseTool.MESH_EDITOR)
				meshBox.onMouseEnter();
		}
	}

	private void onMouseExit(MouseEvent event) {
		if(model.imageBoxModel.imageLoaded) {
			if(model.activeTool.getValue() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseExit(lastMouseDragPoint != null);
			else if(model.activeTool.getValue() == MouseTool.MESH_EDITOR)
				meshBox.onMouseExit();
		}
	}

}
