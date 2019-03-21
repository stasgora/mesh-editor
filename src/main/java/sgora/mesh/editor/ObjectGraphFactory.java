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
import sgora.mesh.editor.model.ImageBoxModel;
import sgora.mesh.editor.model.MeshBoxModel;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.CanvasData;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.model.project.Project;
import sgora.mesh.editor.model.project.VisualProperties;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.*;
import sgora.mesh.editor.services.drawing.CanvasActionFacade;
import sgora.mesh.editor.services.drawing.ColorUtils;
import sgora.mesh.editor.services.drawing.ImageBox;
import sgora.mesh.editor.services.drawing.MeshBox;
import sgora.mesh.editor.services.files.WorkspaceActionFacade;
import sgora.mesh.editor.services.triangulation.FlipBasedTriangulationService;
import sgora.mesh.editor.services.triangulation.FlippingUtils;
import sgora.mesh.editor.services.triangulation.NodeUtils;
import sgora.mesh.editor.services.triangulation.TriangleUtils;
import sgora.mesh.editor.services.files.ProjectFileUtils;
import sgora.mesh.editor.services.files.WorkspaceActionExecutor;
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

	private UiDialogUtils dialogUtils;
	private SettableProperty<MouseTool> activeTool;
	private ObjectProperty<Cursor> mouseCursor;
	private Point canvasViewSize = new Point();

	private ImageBoxModel imageBoxModel;
	private MeshBoxModel meshBoxModel;

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

		//TODO refactor as two way binding
		SettableProperty<Double> meshTransparency = project.visualProperties.meshTransparency;
		meshTransparency.setAndNotify(appConfig.getDouble("default.meshVisibility"));
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
		colorUtils = new ColorUtils(nodeUtils);
	}

	private void createProjectServices() {
		fileUtils = new ProjectFileUtils(project.canvasData, appConfig, project.visualProperties);
		dialogUtils = new UiDialogUtils(stage, appLang);
		workspaceActionExecutor = new WorkspaceActionExecutor(fileUtils, project, this);
		workspaceAction = new WorkspaceActionFacade(workspaceActionExecutor, appLang, dialogUtils, appConfig, project.loadState);
	}

	private void setupVisualObjects() {
		windowView.createWindowScene(appSettings, stage, root);
		activeTool = new SettableProperty<>(MouseTool.MESH_EDITOR);
		mouseCursor = stage.getScene().cursorProperty();
	}

	private void createCanvasBoxServices() {
		//temp
		imageBoxModel = new ImageBoxModel();
		meshBoxModel = new MeshBoxModel();

		imageBox = new ImageBox(canvasViewSize, project.canvasData, appConfig, appSettings, mouseCursor, imageBoxModel);
		meshBox = new MeshBox(project.canvasData.mesh, meshBoxModel, canvasViewSize, mouseCursor, triangulationService, nodeUtils);
		canvasAction = new CanvasActionFacade(project.loadState, imageBox, meshBox, activeTool);
	}

	private void initControllerObjects() {
		propertiesView = new PropertiesView(windowView.propertiesViewRoot, ViewType.PROPERTIES_VIEW, viewNamespaces, project.visualProperties, project.loadState.stateSaved);
		menuView = new MenuView(windowView.menuViewRoot, ViewType.MENU_VIEW, viewNamespaces, workspaceAction, project.loadState);
		canvasView = new CanvasView(windowView.canvasViewRoot, ViewType.CANVAS_VIEW, viewNamespaces, project,
				canvasViewSize, imageBox, nodeUtils, triangleUtils, canvasAction);
		windowView.init(project.loadState, stage, appConfig, workspaceAction);

		canvasView.meshCanvas.init(colorUtils, project.canvasData.baseImage, project.visualProperties);
		windowView.toolBar.init(activeTool, appLang);
	}

}
