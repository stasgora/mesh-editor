package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.SubView;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.model.project.VisualProperties;
import sgora.mesh.editor.services.drawing.ImageBox;
import sgora.mesh.editor.services.drawing.MeshBox;
import sgora.mesh.editor.services.triangulation.NodeUtils;
import sgora.mesh.editor.services.triangulation.TriangleUtils;
import sgora.mesh.editor.ui.canvas.ImageCanvas;
import sgora.mesh.editor.ui.canvas.MeshCanvas;

import java.util.Map;

public class CanvasView extends SubController {

	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;

	private SettableObservable<CanvasData> canvasData;
	private SettableProperty<MouseTool> activeTool;

	private Point canvasViewSize;

	private ImageBox imageBox;
	private MeshBox meshBox;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private SettableObservable<LoadState> loadState;
	private SettableObservable<VisualProperties> visualProperties;

	private Point lastMouseDragPoint;

	public CanvasView(Region root, SubView subView, Map<SubView, ObservableMap<String, Object>> viewNamespaces, SettableObservable<CanvasData> canvasData,
	                  SettableProperty<MouseTool> activeTool, Point canvasViewSize, ImageBox imageBox, MeshBox meshBox, NodeUtils nodeUtils,
	                  TriangleUtils triangleUtils, SettableObservable<LoadState> loadState, SettableObservable<VisualProperties> visualProperties) {
		super(root, subView, viewNamespaces);

		this.canvasData = canvasData;
		this.activeTool = activeTool;
		this.canvasViewSize = canvasViewSize;
		this.imageBox = imageBox;
		this.meshBox = meshBox;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
		this.loadState = loadState;
		this.visualProperties = visualProperties;
		init();
	}

	public void init() {
		setListeners();
		setMouseHandlers();
	}

	private void setListeners() {
		root.widthProperty().addListener(this::paneSizeChanged);
		root.heightProperty().addListener(this::paneSizeChanged);

		canvasViewSize.addListener(() -> {
			imageCanvas.setWidth(canvasViewSize.x);
			imageCanvas.setHeight(canvasViewSize.y);
			meshCanvas.setWidth(canvasViewSize.x);
			meshCanvas.setHeight(canvasViewSize.y);
		});
		CanvasData canvasData = this.canvasData.get();
		canvasViewSize.addListener(() -> imageBox.onResizeCanvas());
		canvasData.baseImage.addListener(() -> imageBox.calcImageBox());
		canvasData.mesh.addStaticListener(() -> loadState.get().stateSaved.set(false));

		canvasViewSize.addListener(this::drawBothLayers);
		canvasData.addListener(this::drawBothLayers);
		canvasData.mesh.addStaticListener(this::drawMesh);

		visualProperties.addStaticListener(this::drawBothLayers);
	}

	private void setMouseHandlers() {
		root.setOnScroll(this::onScroll);

		root.setOnMousePressed(this::onMousePress);
		root.setOnMouseDragged(this::onMouseDrag);

		root.setOnMouseReleased(this::onMouseRelease);
		root.setOnMouseEntered(this::onMouseEnter);
		root.setOnMouseExited(this::onMouseExit);
	}

	private void paneSizeChanged(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
		canvasViewSize.set(new Point(root.getWidth(), root.getHeight()));
		canvasViewSize.notifyListeners();
	}

	private void drawMesh() {
		meshCanvas.clear();
		if(loadState.get().loaded.get() && visualProperties.get().meshVisible.get()) {
			meshCanvas.draw(nodeUtils.getCanvasSpaceNodes(), triangleUtils.getCanvasSpaceTriangles(), nodeUtils.getCanvasSpaceNodeBoundingBox());
		}
	}

	private void drawImage() {
		imageCanvas.clear();
		if(loadState.get().loaded.get() && visualProperties.get().imageVisible.get()) {
			CanvasData canvasData = this.canvasData.get();
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
