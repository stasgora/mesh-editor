package sgora.mesh.editor;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sgora.mesh.editor.config.JsonAppConfigReader;
import sgora.mesh.editor.config.JsonLangConfigReader;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.interfaces.LangConfigReader;
import sgora.mesh.editor.interfaces.TriangulationService;
import sgora.mesh.editor.model.ImageBoxModel;
import sgora.mesh.editor.model.MeshBoxModel;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.Project;
import sgora.mesh.editor.model.project.VisualProperties;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.*;
import sgora.mesh.editor.services.drawing.ColorUtils;
import sgora.mesh.editor.services.drawing.ImageBox;
import sgora.mesh.editor.services.drawing.MeshBox;
import sgora.mesh.editor.services.triangulation.FlipBasedTriangulationService;
import sgora.mesh.editor.services.triangulation.FlippingUtils;
import sgora.mesh.editor.services.triangulation.NodeUtils;
import sgora.mesh.editor.services.triangulation.TriangleUtils;
import sgora.mesh.editor.services.files.ProjectFileUtils;
import sgora.mesh.editor.services.files.WorkspaceActionHandler;
import sgora.mesh.editor.view.WindowController;

public class ObjectGraphFactory {

	private final WindowController controller;
	private final Parent root;
	private final Stage stage;
	private FXMLLoader loader;

	private Project project = new Project();
	private SettableObservable<VisualProperties> visualProperties = new SettableObservable<>();

	private AppConfigReader appConfig;
	private AppConfigReader appSettings;
	private LangConfigReader appLang;

	private UiDialogUtils dialogUtils;
	private WorkspaceActionHandler workspaceActionHandler;
	private FileUtils fileUtils;

	private TriangulationService triangulationService;
	private NodeUtils nodeUtils;
	private TriangleUtils triangleUtils;
	private FlippingUtils flippingUtils;
	private ColorUtils colorUtils;

	private SettableProperty<MouseTool> activeTool;
	private ObjectProperty<Cursor> mouseCursor;
	private Point mainViewSize;

	private ImageBoxModel imageBoxModel;
	private MeshBoxModel meshBoxModel;

	public ObjectGraphFactory(WindowController controller, Parent root, Stage stage, FXMLLoader loader) {
		this.controller = controller;
		this.root = root;
		this.stage = stage;
		this.loader = loader;
	}

	public ObjectGraphFactory buildDependencies() {
		//services
		appConfig = JsonAppConfigReader.forResource("/app.config");
		appSettings = JsonAppConfigReader.forFile("config/app.settings");
		appLang = new JsonLangConfigReader(appConfig, appSettings, loader.getNamespace());

		nodeUtils = new NodeUtils(appConfig, project.imageBox, project.mesh, project.baseImage);
		triangleUtils = new TriangleUtils(project.mesh, nodeUtils);
		flippingUtils = new FlippingUtils(project.mesh, triangleUtils);
		triangulationService = new FlipBasedTriangulationService(project.mesh, nodeUtils, triangleUtils, flippingUtils);
		colorUtils = new ColorUtils(nodeUtils);

		fileUtils = new ProjectFileUtils(project, appConfig, visualProperties);
		workspaceActionHandler = new WorkspaceActionHandler(fileUtils, project, triangulationService, visualProperties);
		dialogUtils = new UiDialogUtils(stage);

		constructScene();

		activeTool = new SettableProperty<>(MouseTool.IMAGE_MOVER);
		mouseCursor = stage.getScene().cursorProperty();
		mainViewSize = new Point();
		//temp
		imageBoxModel = new ImageBoxModel();
		meshBoxModel = new MeshBoxModel();
		return this;
	}

	private void constructScene() {
		//FIXME move logic from factory
		Scene scene;
		String windowPath = "last.windowPlacement";
		if(appSettings.containsPath(windowPath)) {
			scene = new Scene(root, appSettings.getInt(windowPath + ".size.w"), appSettings.getInt(windowPath + ".size.h"));
			stage.setX(appSettings.getInt(windowPath + ".position.x"));
			stage.setY(appSettings.getInt(windowPath + ".position.y"));
		} else {
			scene = new Scene(root, appConfig.getInt("default.windowSize.w"), appConfig.getInt("default.windowSize.h"));
		}
		stage.setScene(scene);
	}

	public void createObjectGraph() {
		ImageBox imageBox = new ImageBox(mainViewSize, project, appConfig, appSettings, mouseCursor, imageBoxModel);
		MeshBox meshBox = new MeshBox(project, meshBoxModel, mainViewSize, mouseCursor, triangulationService, nodeUtils);

		controller.toolBar.init(activeTool, appLang);
		controller.mainView.init(project, controller.imageCanvas, controller.meshCanvas, activeTool, mainViewSize, imageBox, meshBox, nodeUtils, triangleUtils);
		controller.init(project, stage, appConfig, workspaceActionHandler, dialogUtils, loader.getNamespace(), appLang);
		controller.meshCanvas.init(colorUtils, project.baseImage, visualProperties);
	}

}
