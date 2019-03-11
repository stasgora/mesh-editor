package sgora.mesh.editor;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;
import sgora.mesh.editor.config.JsonAppConfigReader;
import sgora.mesh.editor.config.JsonLangConfigReader;
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
import sgora.mesh.editor.view.MainView;
import sgora.mesh.editor.view.PropertiesView;
import sgora.mesh.editor.view.WindowView;

public class ObjectGraphFactory {

	private final WindowView controller;
	private final Parent root;
	private final Stage stage;
	private final FXMLLoader loader;

	private PropertiesView propertiesView;

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
	private Point mainViewSize = new Point();

	private ImageBoxModel imageBoxModel;
	private MeshBoxModel meshBoxModel;

	private ImageBox imageBox;
	private MeshBox meshBox;

	public ObjectGraphFactory(WindowView controller, Parent root, Stage stage, FXMLLoader loader) {
		this.controller = controller;
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
		controller.setupWindow(appSettings, stage, root);

		activeTool = new SettableProperty<>(MouseTool.MESH_EDITOR);
		mouseCursor = stage.getScene().cursorProperty();
	}

	private void createCanvasBoxServices() {
		//temp
		imageBoxModel = new ImageBoxModel();
		meshBoxModel = new MeshBoxModel();

		imageBox = new ImageBox(mainViewSize, canvasData, appConfig, appSettings, mouseCursor, imageBoxModel);
		meshBox = new MeshBox(canvasData.get().mesh, meshBoxModel, mainViewSize, mouseCursor, triangulationService, nodeUtils);
	}

	private void initControllerObjects() {
		MainView mainView = controller.mainViewController;
		propertiesView = new PropertiesView(visualProperties);
		controller.init(loadState, stage, appConfig, workspaceAction, loader.getNamespace(), propertiesView);

		mainView.init(canvasData, mainView.imageCanvas, mainView.meshCanvas, activeTool,
				mainViewSize, imageBox, meshBox, nodeUtils, triangleUtils, loadState, visualProperties);
		mainView.meshCanvas.init(colorUtils, canvasData.get().baseImage, visualProperties);
		controller.toolBar.init(activeTool, appLang);
	}

}
