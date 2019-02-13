package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.ImageBox;
import sgora.mesh.editor.services.MeshBox;
import sgora.mesh.editor.ui.ImageCanvas;
import sgora.mesh.editor.ui.MeshCanvas;

public class MainView extends AnchorPane {
	
	private Project project;

	private ImageCanvas imageCanvas;
	private MeshCanvas meshCanvas;
	private SettableProperty<MouseTool> activeTool;
	private Point mainViewSize;

	private ImageBox imageBox;
	private MeshBox meshBox;

	private Point lastMouseDragPoint;

	public void init(Project project, ImageCanvas imageCanvas, MeshCanvas meshCanvas, SettableProperty<MouseTool> activeTool, Point mainViewSize, ImageBox imageBox, MeshBox meshBox) {
		this.project = project;
		this.imageCanvas = imageCanvas;
		this.meshCanvas = meshCanvas;
		this.activeTool = activeTool;
		this.mainViewSize = mainViewSize;
		this.imageBox = imageBox;
		this.meshBox = meshBox;

		setListeners();
		setMouseHandlers();
	}

	private void setListeners() {
		widthProperty().addListener(this::paneSizeChanged);
		heightProperty().addListener(this::paneSizeChanged);

		mainViewSize.addListener(() -> {
			imageCanvas.setWidth(mainViewSize.x);
			imageCanvas.setHeight(mainViewSize.y);
			meshCanvas.setWidth(mainViewSize.x);
			meshCanvas.setHeight(mainViewSize.y);
		});
		mainViewSize.addListener(() -> imageBox.onResizeCanvas());
		project.baseImage.addListener(() -> imageBox.calcImageBox());
		project.mesh.addStaticListener(() -> project.stateSaved.set(false));

		mainViewSize.addListener(this::drawBothLayers);
		project.imageBox.addListener(this::drawBothLayers);
		project.addListener(this::drawBothLayers);

		project.mesh.addStaticListener(this::drawMesh);
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
		mainViewSize.set(new Point(getWidth(), getHeight()));
		mainViewSize.notifyListeners();
	}

	private void drawMesh() {
		meshCanvas.clear();
		if(project.loaded.get())
			meshCanvas.draw(project.mesh.get(), meshBox.getPixelMeshNodes());
	}

	private void drawImage() {
		imageCanvas.clear();
		if(project.loaded.get())
			imageCanvas.draw(project.imageBox, project.baseImage.get());
	}

	private void drawBothLayers() {
		drawImage();
		drawMesh();
	}

	private void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(project.loaded.get()) {
			if(activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onDragStart(mousePos, event.getButton());
			else if(activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onDragStart(mousePos, event.getButton());
		}
		lastMouseDragPoint = mousePos;
	}

	private void onMouseDrag(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		Point dragAmount = new Point(mousePos).subtract(lastMouseDragPoint);
		if(project.loaded.get()) {
			if (activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseDrag(new Point(dragAmount), event.getButton());
			else if(activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseDrag(new Point(dragAmount), event.getButton());
		}
		lastMouseDragPoint.set(mousePos);
	}

	private void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(project.loaded.get()) {
			if(activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onDragEnd(new Point(mousePos), event.getButton());
			else if(activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onDragEnd(new Point(mousePos), event.getButton());
		}
	}

	private void onScroll(ScrollEvent event) {
		if(project.loaded.get())
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
	}

	private void onMouseEnter(MouseEvent event) {
		if(project.loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseEnter(isDragging);
			else if(activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseEnter(isDragging);
		}
	}

	private void onMouseExit(MouseEvent event) {
		if(project.loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseExit(isDragging);
			else if(activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseExit(isDragging);
		}
	}

}
