package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.interfaces.files.WorkspaceAction;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.services.files.WorkspaceActionFacade;
import sgora.mesh.editor.ui.ImageCanvas;
import sgora.mesh.editor.ui.MainToolbar;
import sgora.mesh.editor.ui.MeshCanvas;

public class WindowController {

	private AppConfigReader appConfig;
	private Project project;
	private Stage window;

	public SplitPane mainSplitPane;
	public MainView mainView;
	public AnchorPane propertiesPane;

	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;
	public MainToolbar toolBar;

	public MenuItem newProjectMenuItem;
	public MenuItem openProjectMenuItem;
	public MenuItem openRecentMenuItem;
	public MenuItem closeProjectMenuItem;
	public MenuItem saveProjectMenuItem;
	public MenuItem saveProjectAsMenuItem;
	public MenuItem exitAppMenuItem;

	private WorkspaceAction workspaceAction;
	private ObservableMap<String, Object> fxmlNamespace;

	public void init(Project project, Stage window, AppConfigReader appConfig, WorkspaceAction workspaceAction, ObservableMap<String, Object> fxmlNamespace) {
		this.project = project;
		this.window = window;
		this.appConfig = appConfig;
		this.workspaceAction = workspaceAction;
		this.fxmlNamespace = fxmlNamespace;
		fxmlNamespace.put("menu_file_item_disabled", true);

		setWindowTitle();
		setListeners();
		window.setOnCloseRequest(workspaceAction::onWindowCloseRequest);
	}

	private void setListeners() {
		project.loaded.addListener(() -> fxmlNamespace.put("menu_file_item_disabled", !((boolean) fxmlNamespace.get("menu_file_item_disabled"))));

		project.file.addListener(this::setWindowTitle);
		project.stateSaved.addListener(this::setWindowTitle);
		project.addListener(this::setWindowTitle);

		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);

		newProjectMenuItem.setOnAction(event -> workspaceAction.onNewProject());
		openProjectMenuItem.setOnAction(event -> workspaceAction.onOpenProject());
		closeProjectMenuItem.setOnAction(event -> workspaceAction.onCloseProject());
		saveProjectMenuItem.setOnAction(event -> workspaceAction.onSaveProject());
		saveProjectAsMenuItem.setOnAction(event -> workspaceAction.onSaveProjectAs());
		exitAppMenuItem.setOnAction(event -> workspaceAction.onExitApp());
	}

	private void setWindowTitle() {
		String title = appConfig.getString("appName");
		if(project.loaded.get()) {
			String projectName = workspaceAction.getProjectName();
			if(!project.stateSaved.get()) {
				projectName += "*";
			}
			title = projectName + " - " + title;
		}
		window.setTitle(title);
	}

	private void keepDividerInPlace(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
		SplitPane.Divider divider = mainSplitPane.getDividers().get(0);
		divider.setPosition(divider.getPosition() * oldVal.doubleValue() / newVal.doubleValue());
	}

}
