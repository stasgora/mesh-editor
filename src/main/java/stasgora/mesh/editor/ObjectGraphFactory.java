package stasgora.mesh.editor;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;
import stasgora.mesh.editor.config.JsonAppConfigReader;
import stasgora.mesh.editor.config.JsonLangConfigReader;
import stasgora.mesh.editor.enums.ViewType;
import stasgora.mesh.editor.interfaces.CanvasAction;
import stasgora.mesh.editor.interfaces.files.FileUtils;
import stasgora.mesh.editor.interfaces.config.AppConfigReader;
import stasgora.mesh.editor.interfaces.config.LangConfigReader;
import stasgora.mesh.editor.interfaces.TriangulationService;
import stasgora.mesh.editor.interfaces.files.WorkspaceAction;
import stasgora.mesh.editor.model.MouseConfig;
import stasgora.mesh.editor.model.project.Project;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.services.files.SvgService;
import stasgora.mesh.editor.services.files.WorkspaceActionFacade;
import stasgora.mesh.editor.services.mapping.ConfigModelMapper;
import stasgora.mesh.editor.services.triangulation.FlipBasedTriangulationService;
import stasgora.mesh.editor.services.triangulation.FlippingUtils;
import stasgora.mesh.editor.services.triangulation.NodeUtils;
import stasgora.mesh.editor.services.triangulation.TriangleUtils;
import stasgora.mesh.editor.services.files.ProjectFileUtils;
import stasgora.mesh.editor.services.files.WorkspaceActionExecutor;
import stasgora.mesh.editor.services.ui.UiDialogUtils;
import stasgora.mesh.editor.view.CanvasView;
import stasgora.mesh.editor.view.MenuView;
import stasgora.mesh.editor.view.PropertiesView;
import stasgora.mesh.editor.view.WindowView;
import stasgora.mesh.editor.services.drawing.CanvasActionFacade;
import stasgora.mesh.editor.services.drawing.ColorUtils;
import stasgora.mesh.editor.services.drawing.ImageBox;
import stasgora.mesh.editor.services.drawing.MeshBox;

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
	private ObjectProperty<Cursor> mouseCursor;
	private Point canvasViewSize = new Point();

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
		createProjectServices();
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

	private void setupVisualObjects() {
		windowView.createWindowScene(appSettings, stage, root);
		mouseCursor = stage.getScene().cursorProperty();
	}

	private void createProjectServices() {
		svgService = new SvgService(project.canvasData, project.visualProperties, nodeUtils, triangleUtils, colorUtils);
		fileUtils = new ProjectFileUtils(project.canvasData, appConfig, project.visualProperties);
		dialogUtils = new UiDialogUtils(stage, appLang);
		workspaceActionExecutor = new WorkspaceActionExecutor(fileUtils, project, this, svgService);
		workspaceAction = new WorkspaceActionFacade(workspaceActionExecutor, appLang, dialogUtils, appConfig, project.loadState, mouseCursor);
		configModelMapper = new ConfigModelMapper(appConfig);
	}

	private void createCanvasBoxServices() {
		imageBox = new ImageBox(canvasViewSize, project.canvasData, appConfig, appSettings, mouseCursor, mouseConfig);
		meshBox = new MeshBox(project.canvasData.mesh, mouseConfig, canvasViewSize, mouseCursor, triangulationService, nodeUtils);
		canvasAction = new CanvasActionFacade(project.loadState, imageBox, meshBox, mouseCursor, mouseConfig);
	}

	private void initControllerObjects() {
		propertiesView = new PropertiesView(windowView.propertiesViewRoot, ViewType.PROPERTIES_VIEW,
				viewNamespaces, project.visualProperties, project.loadState.stateSaved, configModelMapper);
		menuView = new MenuView(windowView.menuViewRoot, ViewType.MENU_VIEW, viewNamespaces, workspaceAction, project.loadState);
		canvasView = new CanvasView(windowView.canvasViewRoot, ViewType.CANVAS_VIEW, viewNamespaces, project,
				canvasViewSize, imageBox, nodeUtils, triangleUtils, canvasAction, project.loadState.loaded);
		windowView.init(project.loadState, stage, appConfig, workspaceAction);

		canvasView.meshCanvas.init(colorUtils, project.visualProperties);
	}

}
