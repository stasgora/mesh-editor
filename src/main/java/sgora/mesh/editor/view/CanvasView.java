package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.ViewType;
import sgora.mesh.editor.interfaces.CanvasAction;
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

	private Point canvasViewSize;

	private ImageBox imageBox;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private SettableObservable<LoadState> loadState;
	private SettableObservable<VisualProperties> visualProperties;
	private final CanvasAction canvasAction;

	public CanvasView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces, SettableObservable<CanvasData> canvasData, 
	                  Point canvasViewSize, ImageBox imageBox, NodeUtils nodeUtils, SettableObservable<LoadState> loadState, 
	                  TriangleUtils triangleUtils, SettableObservable<VisualProperties> visualProperties, CanvasAction canvasAction) {
		super(root, viewType, viewNamespaces);

		this.canvasData = canvasData;
		this.canvasViewSize = canvasViewSize;
		this.imageBox = imageBox;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
		this.loadState = loadState;
		this.visualProperties = visualProperties;
		this.canvasAction = canvasAction;
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
		root.setOnScroll(canvasAction::onScroll);

		root.setOnMousePressed(canvasAction::onMousePress);
		root.setOnMouseDragged(canvasAction::onMouseDrag);

		root.setOnMouseReleased(canvasAction::onMouseRelease);
		root.setOnMouseEntered(canvasAction::onMouseEnter);
		root.setOnMouseExited(canvasAction::onMouseExit);
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

}
