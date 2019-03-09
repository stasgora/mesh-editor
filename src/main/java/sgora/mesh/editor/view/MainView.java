package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.services.drawing.ImageBox;
import sgora.mesh.editor.services.drawing.MeshBox;
import sgora.mesh.editor.services.triangulation.NodeUtils;
import sgora.mesh.editor.services.triangulation.TriangleUtils;
import sgora.mesh.editor.ui.canvas.ImageCanvas;
import sgora.mesh.editor.ui.canvas.MeshCanvas;

public class MainView extends AnchorPane {
	
	private SettableObservable<CanvasData> canvasData;

	private ImageCanvas imageCanvas;
	private MeshCanvas meshCanvas;
	private SettableProperty<MouseTool> activeTool;
	private Point mainViewSize;

	private ImageBox imageBox;
	private MeshBox meshBox;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private SettableObservable<LoadState> loadState;

	private Point lastMouseDragPoint;

	public void init(SettableObservable<CanvasData> canvasData, ImageCanvas imageCanvas, MeshCanvas meshCanvas, SettableProperty<MouseTool> activeTool,
	                 Point mainViewSize, ImageBox imageBox, MeshBox meshBox, NodeUtils nodeUtils, TriangleUtils triangleUtils, SettableObservable<LoadState> loadState) {
		this.canvasData = canvasData;
		this.imageCanvas = imageCanvas;
		this.meshCanvas = meshCanvas;
		this.activeTool = activeTool;
		this.mainViewSize = mainViewSize;
		this.imageBox = imageBox;
		this.meshBox = meshBox;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
		this.loadState = loadState;

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
		CanvasData canvasData = this.canvasData.get();
		mainViewSize.addListener(() -> imageBox.onResizeCanvas());
		canvasData.baseImage.addListener(() -> imageBox.calcImageBox());
		canvasData.mesh.addStaticListener(() -> loadState.get().stateSaved.set(false));

		mainViewSize.addListener(this::drawBothLayers);
		canvasData.imageBox.addListener(this::drawBothLayers);//TODO trigger under canvasData
		canvasData.addListener(this::drawBothLayers);
		canvasData.mesh.addStaticListener(this::drawMesh);
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
		if(loadState.get().loaded.get()) {
			meshCanvas.draw(nodeUtils.getCanvasSpaceNodes(), triangleUtils.getCanvasSpaceTriangles(), nodeUtils.getCanvasSpaceNodeBoundingBox());
		}
	}

	private void drawImage() {
		imageCanvas.clear();
		CanvasData canvasData = this.canvasData.get();
		if(loadState.get().loaded.get()) {
			imageCanvas.draw(canvasData.imageBox, canvasData.baseImage.get());
		}
	}

	private void drawBothLayers() {
		drawImage();
		drawMesh();
	}

	private void onMousePress(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.get().loaded.get()) {
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onDragStart(mousePos, event.getButton());
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onDragStart(mousePos, event.getButton());
			}
		}
		lastMouseDragPoint = mousePos;
	}

	private void onMouseDrag(MouseEvent event) {
		Point mousePos = new Point(event.getX(), event.getY());
		Point dragAmount = new Point(mousePos).subtract(lastMouseDragPoint);
		if(loadState.get().loaded.get()) {
			if (activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onMouseDrag(new Point(dragAmount), mousePos, event.getButton());
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onMouseDrag(new Point(dragAmount), mousePos, event.getButton());
			}
		}
		lastMouseDragPoint.set(mousePos);
	}

	private void onMouseRelease(MouseEvent event) {
		lastMouseDragPoint = null;
		Point mousePos = new Point(event.getX(), event.getY());
		if(loadState.get().loaded.get()) {
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onDragEnd(new Point(mousePos), event.getButton());
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onDragEnd(new Point(mousePos), event.getButton());
			}
		}
	}

	private void onScroll(ScrollEvent event) {
		if(loadState.get().loaded.get()) {
			imageBox.onZoom(event.getDeltaY(), new Point(event.getX(), event.getY()));
		}
	}

	private void onMouseEnter(MouseEvent event) {
		if(loadState.get().loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onMouseEnter(isDragging);
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onMouseEnter(isDragging);
			}
		}
	}

	private void onMouseExit(MouseEvent event) {
		if(loadState.get().loaded.get()) {
			boolean isDragging = lastMouseDragPoint != null;
			if(activeTool.get() == MouseTool.IMAGE_MOVER) {
				imageBox.onMouseExit(isDragging);
			} else if(activeTool.get() == MouseTool.MESH_EDITOR) {
				meshBox.onMouseExit(isDragging);
			}
		}
	}

}
