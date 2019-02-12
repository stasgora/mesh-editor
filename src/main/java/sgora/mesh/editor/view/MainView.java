package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import sgora.mesh.editor.State;
import sgora.mesh.editor.model.containers.ProjectModel;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;
import sgora.mesh.editor.services.ImageBox;
import sgora.mesh.editor.services.MeshBox;
import sgora.mesh.editor.ui.ImageCanvas;
import sgora.mesh.editor.ui.MeshCanvas;

public class MainView extends AnchorPane {

	private ImageCanvas imageCanvas;
	private MeshCanvas meshCanvas;

	private ImageBox imageBox;
	private MeshBox meshBox;

	private State state;

	private Point lastMouseDragPoint;

	void init(State state, ImageCanvas imageCanvas, MeshCanvas meshCanvas) {
		this.state = state;
		this.imageCanvas = imageCanvas;
		this.meshCanvas = meshCanvas;
		
		imageBox = new ImageBox(state);
		meshBox = new MeshBox(state.model);

		setListeners();
		setMouseHandlers();
	}

	private void setListeners() {
		widthProperty().addListener(this::paneSizeChanged);
		heightProperty().addListener(this::paneSizeChanged);

		state.model.mainViewSize.addListener(() -> {
			imageCanvas.setWidth(state.model.mainViewSize.x);
			imageCanvas.setHeight(state.model.mainViewSize.y);
			meshCanvas.setWidth(state.model.mainViewSize.x);
			meshCanvas.setHeight(state.model.mainViewSize.y);
		});
		state.model.mainViewSize.addListener(() -> imageBox.onResizeCanvas());
		project().baseImage.addListener(() -> imageBox.calcImageBox());
		project().mesh.addStaticListener(() -> project().stateSaved.set(false));

		state.model.mainViewSize.addListener(this::drawBothLayers);
		state.model.imageBox.addListener(this::drawBothLayers);
		project().addListener(this::drawBothLayers);

		project().mesh.addStaticListener(this::drawMesh);
	}

	private void setMouseHandlers() {
		setOnScroll(this::onScroll);

		setOnMousePressed(this::onMousePress);
		setOnMouseDragged(this::onMouseDrag);

		setOnMouseReleased(this::onMouseRelease);
		setOnMouseEntered(this::onMouseEnter);
		setOnMouseExited(this::onMouseExit);
	}

	private ProjectModel project() {
		return state.model.project;
	}

	private void paneSizeChanged(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
		state.model.mainViewSize.set(new Point(getWidth(), getHeight()));
		state.model.mainViewSize.notifyListeners();
	}

	private void drawMesh() {
		meshCanvas.clear();
		if(project().loaded.get())
			meshCanvas.draw(project().mesh.get(), meshBox.getPixelMeshNodes());
	}

	private void drawImage() {
		imageCanvas.clear();
		if(project().loaded.get())
			imageCanvas.draw(state.model);
	}

	private void drawBothLayers() {
		drawImage();
		drawMesh();
	}

	private void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(project().loaded.get()) {
			if(state.model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onDragStart(mousePos, event.getButton());
			else if(state.model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onDragStart(mousePos, event.getButton());
		}
		lastMouseDragPoint = mousePos;
	}

	private void onMouseDrag(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		Point dragAmount = new Point(mousePos).subtract(lastMouseDragPoint);
		if(project().loaded.get()) {
			if (state.model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseDrag(new Point(dragAmount), event.getButton());
			else if(state.model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseDrag(new Point(dragAmount), event.getButton());
		}
		lastMouseDragPoint.set(mousePos);
	}

	private void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(project().loaded.get()) {
			if(state.model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onDragEnd(new Point(mousePos), event.getButton());
			else if(state.model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onDragEnd(new Point(mousePos), event.getButton());
		}
	}

	private void onScroll(ScrollEvent event) {
		if(project().loaded.get())
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
	}

	private void onMouseEnter(MouseEvent event) {
		if(project().loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(state.model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseEnter(isDragging);
			else if(state.model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseEnter(isDragging);
		}
	}

	private void onMouseExit(MouseEvent event) {
		if(project().loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(state.model.activeTool.get() == MouseTool.IMAGE_MOVER)
				imageBox.onMouseExit(isDragging);
			else if(state.model.activeTool.get() == MouseTool.MESH_EDITOR)
				meshBox.onMouseExit(isDragging);
		}
	}

}
