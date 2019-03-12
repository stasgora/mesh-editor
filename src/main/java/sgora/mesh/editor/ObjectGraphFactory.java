package sgora.mesh.editor;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;
import sgora.mesh.editor.config.JsonAppConfigReader;
import sgora.mesh.editor.config.JsonLangConfigReader;
import sgora.mesh.editor.enums.SubView;
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
import sgora.mesh.editor.model.project.VisualProperties;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.*;
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
	private final FXMLLoader loader;

	private PropertiesView propertiesView;
	private CanvasView canvasView;
	private MenuView menuView;
	private Map<SubView, ObservableMap<String, Object>> viewNamespaces = new HashMap<>();

	private SettableObservable<LoadState> loadState = new SettableObservable<>(new LoadState());
	private SettableObservable<VisualProperties> visualProperties = new SettableObservable<>();
	private SettableObservable<CanvasData> canvasData = new SettableObservable<>(new CanvasData());

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

	public ObjectGraphFactory(WindowView controller, Parent root, Stage stage, FXMLLoader loader) {
		this.windowView = controller;
		this.root = root;
		this.stage = stage;
		this.loader = loader;
	}

	public void createProjectModel() {
		triangulationService.createNewMesh();
		visualProperties.set(new VisualProperties());
	}

	public void createObjectGraph() {
		createConfigServices();
		createTriangulationServices();
		createProjectServices();
		setupVisualObjects();
		createCanvasBoxServices();
		initControllerObjects();
	}

	private void createConfigServices() {
		appConfig = JsonAppConfigReader.forResource("/app.config");
		appSettings = JsonAppConfigReader.forFile("config/app.settings");
		appLang = new JsonLangConfigReader(appConfig, appSettings, loader.getNamespace());
	}

	private void createTriangulationServices() {
		nodeUtils = new NodeUtils(appConfig, canvasData);
		triangleUtils = new TriangleUtils(canvasData.get().mesh, nodeUtils);
		flippingUtils = new FlippingUtils(canvasData.get().mesh, triangleUtils);
		triangulationService = new FlipBasedTriangulationService(canvasData.get().mesh, nodeUtils, triangleUtils, flippingUtils);
		colorUtils = new ColorUtils(nodeUtils);
	}

	private void createProjectServices() {
		fileUtils = new ProjectFileUtils(canvasData, appConfig, visualProperties);
		dialogUtils = new UiDialogUtils(stage, appLang);
		workspaceActionExecutor = new WorkspaceActionExecutor(fileUtils, loadState, this, canvasData);
		workspaceAction = new WorkspaceActionFacade(workspaceActionExecutor, appLang, dialogUtils, appConfig, loadState);
	}

	private void setupVisualObjects() {
		dialogUtils = new UiDialogUtils(stage, appLang);
		windowView.setupWindow(appSettings, stage, root);

		activeTool = new SettableProperty<>(MouseTool.MESH_EDITOR);
		mouseCursor = stage.getScene().cursorProperty();
	}

	private void createCanvasBoxServices() {
		//temp
		imageBoxModel = new ImageBoxModel();
		meshBoxModel = new MeshBoxModel();

		imageBox = new ImageBox(canvasViewSize, canvasData, appConfig, appSettings, mouseCursor, imageBoxModel);
		meshBox = new MeshBox(canvasData.get().mesh, meshBoxModel, canvasViewSize, mouseCursor, triangulationService, nodeUtils);
	}

	private void initControllerObjects() {
		propertiesView = new PropertiesView(windowView.propertiesViewRoot, SubView.PROPERTIES_VIEW, viewNamespaces, visualProperties);
		menuView = new MenuView(windowView.menuViewRoot, SubView.MENU_VIEW, viewNamespaces, workspaceAction, loadState);
		canvasView = new CanvasView(windowView.canvasViewRoot, SubView.CANVAS_VIEW, viewNamespaces, canvasData, activeTool,
				canvasViewSize, imageBox, meshBox, nodeUtils, triangleUtils, loadState, visualProperties);
		windowView.init(loadState, stage, appConfig, workspaceAction, loader.getNamespace());

		canvasView.meshCanvas.init(colorUtils, canvasData.get().baseImage, visualProperties);
		windowView.toolBar.init(activeTool, appLang);
	}

}
