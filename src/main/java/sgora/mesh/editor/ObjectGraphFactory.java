package sgora.mesh.editor;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sgora.mesh.editor.config.JsonConfigReader;
import sgora.mesh.editor.interfaces.ConfigReader;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.containers.ImageBoxModel;
import sgora.mesh.editor.model.containers.MeshBoxModel;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.enums.MouseTool;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.services.*;
import sgora.mesh.editor.view.WindowController;

public class ObjectGraphFactory {

	private final WindowController controller;
	private final Parent root;
	private final Stage stage;

	private Project project = new Project();

	private ConfigReader appConfig;
	private ConfigReader appSettings;
	private UiDialogUtils dialogUtils;
	private WorkspaceActionHandler workspaceActionHandler;
	private FileUtils fileUtils;

	private SettableProperty<MouseTool> activeTool;
	private ObjectProperty<Cursor> mouseCursor;
	private Point mainViewSize;

	private ImageBoxModel imageBoxModel;
	private MeshBoxModel meshBoxModel;

	public ObjectGraphFactory(WindowController controller, Parent root, Stage stage) {
		this.controller = controller;
		this.root = root;
		this.stage = stage;
	}

	public ObjectGraphFactory buildDependencies() {
		//services
		appConfig = JsonConfigReader.forResourceFile("/app.config");
		appSettings = JsonConfigReader.forFile("config/app.settings");
		fileUtils = new ProjectFileUtils(project, appConfig);
		workspaceActionHandler = new WorkspaceActionHandler(fileUtils, project);
		dialogUtils = new UiDialogUtils(stage);

		Scene scene;
		String windowPath = "last.windowPlacement";
		if(appSettings.containsPath(windowPath)) {
			scene = new Scene(root, appSettings.getInt(windowPath + ".size.w"), appSettings.getInt(windowPath + ".size.h"));
			stage.setX(appSettings.getInt(windowPath + ".position.x"));
			stage.setY(appSettings.getInt(windowPath + ".position.y"));
		} else {
			scene = new Scene(root, appConfig.getInt("defaultWindowSize.w"), appConfig.getInt("defaultWindowSize.h"));
		}
		stage.setScene(scene);

		activeTool = new SettableProperty<>(MouseTool.IMAGE_MOVER);
		mouseCursor = stage.getScene().cursorProperty();
		mainViewSize = new Point();

		//temp
		imageBoxModel = new ImageBoxModel();
		meshBoxModel = new MeshBoxModel();

		return this;
	}

	public void createObjectGraph() {
		ImageBox imageBox = new ImageBox(mainViewSize, project, appConfig, appSettings, mouseCursor, imageBoxModel);
		MeshBox meshBox = new MeshBox(project, meshBoxModel, mainViewSize, mouseCursor);

		controller.toolBar.init(activeTool);
		controller.mainView.init(project, controller.imageCanvas, controller.meshCanvas, activeTool, mainViewSize, imageBox, meshBox);
		controller.init(project, stage, appConfig, workspaceActionHandler, dialogUtils);
	}

}
