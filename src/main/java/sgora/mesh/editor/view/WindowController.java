package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.interfaces.files.WorkspaceAction;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.ui.MainToolbar;
import sgora.mesh.editor.ui.canvas.ImageCanvas;
import sgora.mesh.editor.ui.canvas.MeshCanvas;

public class WindowController {

	public PropertiesView propertiesViewController;
	public VBox propertiesView;

	private AppConfigReader appConfig;
	private SettableObservable<LoadState> loadState;
	private Stage window;

	public SplitPane mainSplitPane;
	public MainView mainView;

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

	private static final String MENU_FILE_ITEM_DISABLED = "menu_file_item_disabled";

	public void init(SettableObservable<LoadState> loadState, Stage window, AppConfigReader appConfig, WorkspaceAction workspaceAction, ObservableMap<String, Object> fxmlNamespace) {
		this.loadState = loadState;
		this.window = window;
		this.appConfig = appConfig;
		this.workspaceAction = workspaceAction;
		this.fxmlNamespace = fxmlNamespace;
		fxmlNamespace.put(MENU_FILE_ITEM_DISABLED, true);

		setWindowTitle();
		setListeners();
		window.setOnCloseRequest(workspaceAction::onWindowCloseRequest);
	}

	private void setListeners() {
		LoadState loadState = this.loadState.get();
		loadState.loaded.addListener(() -> fxmlNamespace.put(MENU_FILE_ITEM_DISABLED, !((boolean) fxmlNamespace.get(MENU_FILE_ITEM_DISABLED))));
		loadState.loaded.addListener(this::setWindowTitle);
		loadState.file.addListener(this::setWindowTitle);
		loadState.stateSaved.addListener(this::setWindowTitle);
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
		loadState.loaded.addListener(() -> propertiesView.setVisible(loadState.loaded.get()));

		newProjectMenuItem.setOnAction(event -> workspaceAction.onNewProject());
		openProjectMenuItem.setOnAction(event -> workspaceAction.onOpenProject());
		closeProjectMenuItem.setOnAction(event -> workspaceAction.onCloseProject());
		saveProjectMenuItem.setOnAction(event -> workspaceAction.onSaveProject());
		saveProjectAsMenuItem.setOnAction(event -> workspaceAction.onSaveProjectAs());
		exitAppMenuItem.setOnAction(event -> workspaceAction.onExitApp());
	}

	private void setWindowTitle() {
		String title = appConfig.getString("appName");
		LoadState loadState = this.loadState.get();
		if(loadState.loaded.get()) {
			String projectName = workspaceAction.getProjectName();
			if(!loadState.stateSaved.get()) {
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

	public void setupWindow(AppConfigReader appSettings, Stage stage, Parent root) {
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

}
