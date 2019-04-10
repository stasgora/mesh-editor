package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.ViewType;
import sgora.mesh.editor.interfaces.CanvasAction;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.observables.ListenerPriority;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.project.Project;
import sgora.mesh.editor.services.drawing.ImageBox;
import sgora.mesh.editor.services.triangulation.NodeUtils;
import sgora.mesh.editor.services.triangulation.TriangleUtils;
import sgora.mesh.editor.ui.canvas.ImageCanvas;
import sgora.mesh.editor.ui.canvas.MeshCanvas;

import java.util.Map;

public class CanvasView extends SubController {

	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;

	private final Project project;
	private Point canvasViewSize;

	private ImageBox imageBox;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private final CanvasAction canvasAction;
	private final SettableProperty<Boolean> loaded;

	public CanvasView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces, Project project, Point canvasViewSize,
	                  ImageBox imageBox, NodeUtils nodeUtils, TriangleUtils triangleUtils, CanvasAction canvasAction, SettableProperty<Boolean> loaded) {
		super(root, viewType, viewNamespaces);

		this.project = project;
		this.canvasViewSize = canvasViewSize;
		this.imageBox = imageBox;
		this.nodeUtils = nodeUtils;
		this.triangleUtils = triangleUtils;
		this.canvasAction = canvasAction;
		this.loaded = loaded;
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
		CanvasData canvasData = project.canvasData;
		canvasViewSize.addListener(() -> imageBox.onResizeCanvas());
		canvasData.mesh.addStaticListener(() -> project.loadState.stateSaved.setAndNotify(false));

		canvasViewSize.addListener(this::drawBothLayers);
		canvasData.addListener(this::drawBothLayers);
		project.visualProperties.addListener(this::drawMesh);

		project.visualProperties.addListener(this::drawBothLayers);
		loaded.addListener(() -> {
			if(!loaded.get()) return;
			imageBox.calcImageBox();

		}, ListenerPriority.HIGH);
	}

	private void setMouseHandlers() {
		root.setOnScroll(canvasAction::onScroll);
		root.setOnMouseMoved(canvasAction::onMouseMove);

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
		if(project.loadState.loaded.get() && project.visualProperties.meshVisible.get()) {
			meshCanvas.draw(nodeUtils.getCanvasSpaceNodes(), triangleUtils.getCanvasSpaceTriangles(), nodeUtils.getCanvasSpaceNodeBoundingBox());
		}
	}

	private void drawImage() {
		imageCanvas.clear();
		if(project.loadState.loaded.get() && project.visualProperties.imageVisible.get()) {
			CanvasData canvasData = project.canvasData;
			imageCanvas.draw(canvasData.imageBox, canvasData.baseImage.get());
		}
	}

	private void drawBothLayers() {
		drawImage();
		drawMesh();
	}

}
