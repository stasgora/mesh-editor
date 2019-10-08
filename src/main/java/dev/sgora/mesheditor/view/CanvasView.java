package dev.sgora.mesheditor.view;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.model.project.CanvasData;
import dev.sgora.mesheditor.model.project.LoadState;
import dev.sgora.mesheditor.model.project.VisualProperties;
import dev.sgora.mesheditor.services.drawing.ImageBox;
import io.github.stasgora.observetree.enums.ListenerPriority;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import dev.sgora.mesheditor.model.NamespaceMap;
import dev.sgora.mesheditor.model.project.CanvasUI;
import dev.sgora.mesheditor.services.input.CanvasAction;
import dev.sgora.mesheditor.services.mesh.generation.NodeUtils;
import dev.sgora.mesheditor.services.mesh.rendering.CanvasRenderer;
import dev.sgora.mesheditor.ui.canvas.ImageCanvas;
import dev.sgora.mesheditor.ui.canvas.ResizableCanvas;
import dev.sgora.mesheditor.view.sub.SubView;

public class CanvasView extends SubView {
	@FXML
	private ImageCanvas imageCanvas;
	@FXML
	private ResizableCanvas meshCanvas;

	private final VisualProperties visualProperties;
	private Point canvasViewSize;

	private final CanvasData canvasData;
	private ImageBox imageBox;
	private NodeUtils nodeUtils;
	private final CanvasAction canvasAction;
	private final LoadState loadState;
	private final CanvasRenderer canvasMeshRenderer;

	@Inject
	CanvasView(@Assisted Region root, @Assisted ViewType viewType, NamespaceMap viewNamespaces, VisualProperties visualProperties, CanvasUI canvasUI,
	           CanvasData canvasData, ImageBox imageBox, NodeUtils nodeUtils, CanvasAction canvasAction, LoadState loadState, CanvasRenderer canvasMeshRenderer) {
		super(root, viewType, viewNamespaces);

		this.visualProperties = visualProperties;
		this.canvasViewSize = canvasUI.canvasViewSize;
		this.canvasData = canvasData;
		this.imageBox = imageBox;
		this.nodeUtils = nodeUtils;
		this.canvasAction = canvasAction;
		this.loadState = loadState;
		this.canvasMeshRenderer = canvasMeshRenderer;

		imageCanvas.init(canvasData, visualProperties.imageTransparency);
		canvasMeshRenderer.setContext(meshCanvas.getGraphicsContext2D());
		init();
	}

	@Override
	protected void init() {
		setListeners();
		setMouseHandlers();
	}

	private void setListeners() {
		root.widthProperty().addListener(this::paneSizeChanged);
		root.heightProperty().addListener(this::paneSizeChanged);

		canvasViewSize.addListener(() -> {
			imageCanvas.setWidth(canvasViewSize.getX());
			imageCanvas.setHeight(canvasViewSize.getY());
			meshCanvas.setWidth(canvasViewSize.getX());
			meshCanvas.setHeight(canvasViewSize.getY());
		});
		canvasViewSize.addListener(() -> imageBox.onResizeCanvas());
		canvasData.mesh.addStaticListener(() -> loadState.stateSaved.setAndNotify(false));

		canvasViewSize.addListener(this::drawBothLayers);
		canvasData.addListener(this::drawBothLayers);
		visualProperties.addListener(this::drawMesh);

		visualProperties.addListener(this::drawBothLayers);
		loadState.loaded.addListener(() -> {
			if (!loadState.loaded.get()) return;
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
		if (loadState.loaded.get() && visualProperties.meshVisible.get()) {
			canvasMeshRenderer.render();
			canvasMeshRenderer.drawBoundingBox(nodeUtils.getCanvasSpaceNodeBoundingBox());
		}
	}

	private void drawImage() {
		imageCanvas.clear();
		if (loadState.loaded.get() && visualProperties.imageVisible.get()) {
			imageCanvas.draw();
		}
	}

	private void drawBothLayers() {
		drawImage();
		drawMesh();
	}

}
