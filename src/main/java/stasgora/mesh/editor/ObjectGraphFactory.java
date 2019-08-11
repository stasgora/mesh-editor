package stasgora.mesh.editor;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;
import stasgora.mesh.editor.model.MouseConfig;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.JsonAppConfigReader;
import stasgora.mesh.editor.services.config.JsonLangConfigReader;
import stasgora.mesh.editor.services.config.LangConfigReader;
import stasgora.mesh.editor.services.drawing.ColorUtils;
import stasgora.mesh.editor.services.drawing.ImageBox;
import stasgora.mesh.editor.services.drawing.MeshBox;
import stasgora.mesh.editor.services.files.FileUtils;
import stasgora.mesh.editor.services.files.ProjectFileUtils;
import stasgora.mesh.editor.services.files.workspace.WorkspaceAction;
import stasgora.mesh.editor.services.files.workspace.WorkspaceActionExecutor;
import stasgora.mesh.editor.services.files.workspace.WorkspaceActionFacade;
import stasgora.mesh.editor.services.history.ActionHistoryService;
import stasgora.mesh.editor.services.history.CommandActionHistoryService;
import stasgora.mesh.editor.services.history.actions.node.NodeModifiedAction;
import stasgora.mesh.editor.services.input.CanvasAction;
import stasgora.mesh.editor.services.input.CanvasActionFacade;
import stasgora.mesh.editor.services.mapping.ConfigModelMapper;
import stasgora.mesh.editor.services.mesh.rendering.CanvasMeshRenderer;
import stasgora.mesh.editor.services.mesh.rendering.SvgMeshRenderer;
import stasgora.mesh.editor.services.mesh.triangulation.*;
import stasgora.mesh.editor.services.mesh.voronoi.VoronoiDiagramService;
import stasgora.mesh.editor.services.ui.PropertyTreeCellFactory;
import stasgora.mesh.editor.services.ui.UiDialogUtils;
import stasgora.mesh.editor.view.*;

import java.util.HashMap;
import java.util.Map;

public class ObjectGraphFactory {

	private final WindowView windowView;
	private final Parent root;
	private final Stage stage;
	private AboutWindow aboutWindow;

	private PropertiesView propertiesView;
	private CanvasView canvasView;
	private MenuView menuView;

	private Map<String, ObservableMap<String, Object>> viewNamespaces = new HashMap<>();

	private Project project = new Project(loadState, visualProperties, canvasData);

	private AppConfigReader appConfig;
	private AppConfigReader appSettings;
	private LangConfigReader appLang;

	private WorkspaceAction workspaceAction;
	private ActionHistoryService actionHistoryService;
	private WorkspaceActionExecutor workspaceActionExecutor;
	private FileUtils fileUtils;

	private TriangulationService triangulationService;
	private VoronoiDiagramService voronoiDiagramService;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private FlippingUtils flippingUtils;

	private ColorUtils colorUtils;
	private CanvasMeshRenderer canvasMeshRenderer;
	private SvgMeshRenderer svgMeshRenderer;

	private UiDialogUtils dialogUtils;
	private ObjectProperty<Cursor> mouseCursor;
	private Point canvasViewSize = new Point();
	private PropertyTreeCellFactory propertyTreeCellFactory;

	private ConfigModelMapper configModelMapper;

	private MouseConfig mouseConfig = new MouseConfig();

	private ImageBox imageBox;
	private MeshBox meshBox;
	private CanvasAction canvasAction;

	public ObjectGraphFactory(WindowView controller, Parent root, Stage stage, ObservableMap<String, Object> windowNamespace) {
		this.windowView = controller;
		this.root = root;
		this.stage = stage;
		viewNamespaces.put(ViewType.WINDOW_VIEW.langPrefix, windowNamespace);
	}

	public void createProjectModel() {
		imageBox.calcImageBox();
		triangulationService.createNewMesh();
		project.visualProperties.restoreDefaultValues();
	}

	public void createObjectGraph() {
		createConfigServices();
		createTriangulationServices();
		setupVisualObjects();
		createRenderingServices();

		createActionHistoryService();
		createProjectServices();

		createCanvasBoxServices();
		initControllerObjects();

		appLang.onSetMainLanguage();
	}

	private void createConfigServices() { //✓
		appConfig = JsonAppConfigReader.forResource("/app.config");
		appSettings = JsonAppConfigReader.forFile("config/app.settings");
		appLang = new JsonLangConfigReader(appConfig, appSettings, viewNamespaces);
	}

	private void createTriangulationServices() {
		nodeUtils = new NodeUtils(appConfig, project.canvasData);
		triangleUtils = new TriangleUtils(project.canvasData.mesh, nodeUtils);
		flippingUtils = new FlippingUtils(project.canvasData.mesh, triangleUtils);
		voronoiDiagramService = new VoronoiDiagramService(project.canvasData.mesh, nodeUtils);
		triangulationService = new FlipBasedTriangulationService(project.canvasData.mesh, nodeUtils, triangleUtils, flippingUtils, voronoiDiagramService);
	}

	private void setupVisualObjects() {
		windowView.createWindowScene(appSettings, stage, root);
		mouseCursor = stage.getScene().cursorProperty();
		aboutWindow = new AboutWindow(stage, appConfig);
	}

	private void createRenderingServices() {
		colorUtils = new ColorUtils(nodeUtils, project.canvasData.baseImage, appConfig);

		canvasMeshRenderer = new CanvasMeshRenderer(triangleUtils, nodeUtils, colorUtils, project.visualProperties);
		svgMeshRenderer = new SvgMeshRenderer(triangleUtils, nodeUtils, colorUtils, project.visualProperties);
	}

	private void createProjectServices() {
		fileUtils = new ProjectFileUtils(project.canvasData, appConfig, project.visualProperties);
		dialogUtils = new UiDialogUtils(stage, appLang);
		workspaceActionExecutor = new WorkspaceActionExecutor(fileUtils, project, this, svgMeshRenderer);
		workspaceAction = new WorkspaceActionFacade(workspaceActionExecutor, appLang, dialogUtils, appConfig, project.loadState, mouseCursor);
		configModelMapper = new ConfigModelMapper(appConfig);
		propertyTreeCellFactory = new PropertyTreeCellFactory(appLang, appConfig, project.visualProperties, actionHistoryService);
	}

	private void createActionHistoryService() {
		actionHistoryService = new CommandActionHistoryService(project.loadState);
		NodeModifiedAction.setNodeMethodReferences(triangulationService::addNode, triangulationService::removeNode);
	}

	private void createCanvasBoxServices() {
		imageBox = new ImageBox(canvasViewSize, project.canvasData, appConfig, appSettings, mouseCursor, mouseConfig);
		meshBox = new MeshBox(project.canvasData.mesh, mouseConfig, canvasViewSize, mouseCursor, triangulationService, nodeUtils, actionHistoryService);
		canvasAction = new CanvasActionFacade(project.loadState, imageBox, meshBox, mouseCursor, mouseConfig);
	}

	private void initControllerObjects() {
		propertiesView = new PropertiesView(windowView.propertiesViewRoot, ViewType.PROPERTIES_VIEW,
				viewNamespaces, project.visualProperties, project.loadState.stateSaved, configModelMapper, propertyTreeCellFactory);
		menuView = new MenuView(windowView.menuViewRoot, ViewType.MENU_VIEW, viewNamespaces, workspaceAction, project.loadState, actionHistoryService, aboutWindow);
		canvasView = new CanvasView(windowView.canvasViewRoot, ViewType.CANVAS_VIEW, viewNamespaces, project,
				canvasViewSize, imageBox, nodeUtils, triangleUtils, canvasAction, project.loadState.loaded, canvasMeshRenderer);
		windowView.init(project.loadState, stage, appConfig, workspaceAction);

		canvasView.imageCanvas.init(project.canvasData, project.visualProperties.imageTransparency);
	}

}
