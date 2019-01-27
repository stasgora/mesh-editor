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

	ImageBox imageBox;
	MeshBox meshBox;

	private Model model;

	private Point lastMouseDragPoint;

	void init(Model model, ImageCanvas imageCanvas, MeshCanvas meshCanvas) {
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

		model.mainViewSize.addListener(() -> {
			imageCanvas.setWidth(model.mainViewSize.x);
			imageCanvas.setHeight(model.mainViewSize.y);
			meshCanvas.setWidth(model.mainViewSize.x);
			meshCanvas.setHeight(model.mainViewSize.y);
		});
		model.mainViewSize.addListener(() -> imageBox.onResizeCanvas());
		model.mainViewSize.addListener(this::drawBothLayers);
		model.imageBoxModel.imageBox.addListener(this::drawBothLayers);
		model.project.loaded.addListener(this::drawBothLayers);
		model.meshBoxModel.addListener(this::drawMesh);
	}

	private void setMouseHandlers() {
		setOnScroll(this::onScroll);

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
		meshCanvas.draw(model.meshBoxModel, meshBox.getMeshNodes(), model.project.loaded.get());
	}

	private void drawImage() {
		imageCanvas.draw(model.imageBoxModel, model.project.loaded.get());
	}

	private void drawBothLayers() {
		drawImage();
		drawMesh();
	}

	private void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(model.project.loaded.get()) {
			if(model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onDragStart(mousePos, event.getButton());
			else if(model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onDragStart(mousePos, event.getButton());
		}
		lastMouseDragPoint = mousePos;
	}

	private void onMouseDrag(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		Point dragAmount = new Point(mousePos).subtract(lastMouseDragPoint);
		if(model.project.loaded.get()) {
			if (model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseDrag(new Point(dragAmount), event.getButton());
			else if(model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseDrag(new Point(dragAmount), event.getButton());
		}
		lastMouseDragPoint.set(mousePos);
	}

	private void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(model.project.loaded.get()) {
			if(model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onDragEnd(new Point(mousePos), event.getButton());
			else if(model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onDragEnd(new Point(mousePos), event.getButton());
		}
	}

	private void onScroll(ScrollEvent event) {
		if(model.project.loaded.get())
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
	}

	private void onMouseEnter(MouseEvent event) {
		if(model.project.loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseEnter(isDragging);
			else if(model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseEnter(isDragging);
		}
	}

	private void onMouseExit(MouseEvent event) {
		if(model.project.loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseExit(isDragging);
			else if(model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseExit(isDragging);
		}
	}

}