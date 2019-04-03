package sgora.mesh.editor;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;
import sgora.mesh.editor.config.JsonAppConfigReader;
import sgora.mesh.editor.config.JsonLangConfigReader;
import sgora.mesh.editor.enums.ViewType;
import sgora.mesh.editor.interfaces.CanvasAction;
import sgora.mesh.editor.interfaces.files.FileUtils;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.interfaces.config.LangConfigReader;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.interfaces.files.WorkspaceAction;
import sgora.mesh.editor.model.KeysConfig;
import sgora.mesh.editor.model.project.Project;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.drawing.*;
import sgora.mesh.editor.services.files.SvgService;
import sgora.mesh.editor.services.files.WorkspaceActionFacade;
import sgora.mesh.editor.services.mapping.ConfigModelMapper;
import sgora.mesh.editor.services.triangulation.FlipBasedTriangulationService;
import sgora.mesh.editor.services.triangulation.FlippingUtils;
import sgora.mesh.editor.services.triangulation.NodeUtils;
import sgora.mesh.editor.services.triangulation.TriangleUtils;
import sgora.mesh.editor.services.files.ProjectFileUtils;
import sgora.mesh.editor.services.files.WorkspaceActionExecutor;
import sgora.mesh.editor.services.ui.UiDialogUtils;
import sgora.mesh.editor.view.CanvasView;
import sgora.mesh.editor.view.MenuView;
import sgora.mesh.editor.view.PropertiesView;
import sgora.mesh.editor.view.WindowView;

import java.util.HashMap;
import java.util.Map;

public class ObjectGraphFactory {

	private final WindowView windowView;
	private final Parent root;
	private final Stage stage;

	private PropertiesView propertiesView;
	private CanvasView canvasView;
	private MenuView menuView;
	private Map<String, ObservableMap<String, Object>> viewNamespaces = new HashMap<>();

	private Project project = new Project();

	private AppConfigReader appConfig;
	private AppConfigReader appSettings;
	private LangConfigReader appLang;

	private WorkspaceAction workspaceAction;
	private WorkspaceActionExecutor workspaceActionExecutor;
	private FileUtils fileUtils;

	private TriangulationService triangulationService;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private FlippingUtils flippingUtils;
	private ColorUtils colorUtils;
	private SvgService svgService;

	private UiDialogUtils dialogUtils;
	private SettableProperty<MouseTool> activeTool;
	private ObjectProperty<Cursor> mouseCursor;
	private Point canvasViewSize = new Point();

	private ConfigModelMapper configModelMapper;

	private KeysConfig keysConfig;

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
		propertiesView.setPropertiesDefaultValues();
	}

	public void createObjectGraph() {
		createConfigServices();
		createTriangulationServices();
		createProjectServices();
		setupVisualObjects();
		createCanvasBoxServices();
		initControllerObjects();

		appLang.onSetMainLanguage();
	}

	private void createConfigServices() {
		appConfig = JsonAppConfigReader.forResource("/app.config");
		appSettings = JsonAppConfigReader.forFile("config/app.settings");
		appLang = new JsonLangConfigReader(appConfig, appSettings, viewNamespaces);
	}

	private void createTriangulationServices() {
		nodeUtils = new NodeUtils(appConfig, project.canvasData);
		triangleUtils = new TriangleUtils(project.canvasData.mesh, nodeUtils);
		flippingUtils = new FlippingUtils(project.canvasData.mesh, triangleUtils);
		triangulationService = new FlipBasedTriangulationService(project.canvasData.mesh, nodeUtils, triangleUtils, flippingUtils);
		colorUtils = new ColorUtils(nodeUtils, project.canvasData.baseImage, appConfig);
	}

	private void createProjectServices() {
		svgService = new SvgService(project.canvasData, project.visualProperties, nodeUtils, triangleUtils, colorUtils);
		fileUtils = new ProjectFileUtils(project.canvasData, appConfig, project.visualProperties);
		dialogUtils = new UiDialogUtils(stage, appLang);
		workspaceActionExecutor = new WorkspaceActionExecutor(fileUtils, project, this, svgService, dialogUtils);
		workspaceAction = new WorkspaceActionFacade(workspaceActionExecutor, appLang, dialogUtils, appConfig, project.loadState);
		configModelMapper = new ConfigModelMapper(appConfig);
	}

	private void setupVisualObjects() {
		windowView.createWindowScene(appSettings, stage, root);
		activeTool = new SettableProperty<>(MouseTool.MESH_EDITOR);
		mouseCursor = stage.getScene().cursorProperty();
	}

	private void createCanvasBoxServices() {
		keysConfig = new KeysConfig();
		imageBox = new ImageBox(canvasViewSize, project.canvasData, appConfig, appSettings, mouseCursor, keysConfig);
		meshBox = new MeshBox(project.canvasData.mesh, keysConfig, canvasViewSize, mouseCursor, triangulationService, nodeUtils);
		canvasAction = new CanvasActionFacade(project.loadState, imageBox, meshBox, activeTool);
	}

	private void initControllerObjects() {
		propertiesView = new PropertiesView(windowView.propertiesViewRoot, ViewType.PROPERTIES_VIEW,
				viewNamespaces, project.visualProperties, project.loadState.stateSaved, configModelMapper);
		menuView = new MenuView(windowView.menuViewRoot, ViewType.MENU_VIEW, viewNamespaces, workspaceAction, project.loadState);
		canvasView = new CanvasView(windowView.canvasViewRoot, ViewType.CANVAS_VIEW, viewNamespaces, project,
				canvasViewSize, imageBox, nodeUtils, triangleUtils, canvasAction, project.loadState.loaded);
		windowView.init(project.loadState, stage, appConfig, workspaceAction);

		canvasView.meshCanvas.init(colorUtils, project.visualProperties);
		windowView.toolBar.init(activeTool, appLang);
	}

}
