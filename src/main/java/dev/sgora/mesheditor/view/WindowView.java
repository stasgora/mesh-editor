package dev.sgora.mesheditor.view;

import com.google.inject.Inject;
import dev.sgora.mesheditor.model.project.LoadState;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dev.sgora.mesheditor.MeshEditor;
import dev.sgora.mesheditor.services.config.annotation.AppSettings;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigReader;
import dev.sgora.mesheditor.services.config.annotation.AppConfig;
import dev.sgora.mesheditor.services.files.workspace.interfaces.WorkspaceAction;
import dev.sgora.mesheditor.view.annotation.MainWindowRoot;
import dev.sgora.mesheditor.view.annotation.MainWindowStage;

public class WindowView {
	@FXML
	private AnchorPane propertiesViewRoot;
	@FXML
	private StackPane canvasViewRoot;
	@FXML
	private MenuBar menuViewRoot;

	private AppConfigReader appConfig;
	private LoadState loadState;
	private Stage window;

	@FXML
	private VBox root;
	@FXML
	private SplitPane mainSplitPane;

	private WorkspaceAction workspaceAction;

	@Inject
	void init(LoadState loadState, @MainWindowStage Stage window, @AppConfig AppConfigReader appConfig,
	          @AppSettings AppConfigManager appSettings, @MainWindowRoot Parent windowRoot) {
		this.loadState = loadState;
		this.window = window;
		this.appConfig = appConfig;

		setWindowTitle();
		setListeners();
		createWindowScene(appSettings, windowRoot);
	}

	public void setWorkspaceAction(WorkspaceAction workspaceAction) {
		this.workspaceAction = workspaceAction;
		window.setOnCloseRequest(workspaceAction::onWindowCloseRequest);
	}

	private void setListeners() {
		loadState.addListener(this::setWindowTitle);
		SplitPane.setResizableWithParent(mainSplitPane.getItems().get(0), false);
		loadState.loaded.addListener(() -> propertiesViewRoot.setVisible(loadState.loaded.get()));
	}

	private void setWindowTitle() {
		String title = appConfig.getString("app.name");
		if (loadState.loaded.get()) {
			String projectName = workspaceAction.getProjectName();
			if (!loadState.stateSaved.get()) {
				projectName += "*";
			}
			title = projectName + " - " + title;
		}
		window.setTitle(title);
	}

	private void createWindowScene(AppConfigReader appSettings, Parent windowRoot) {
		Scene scene;
		String windowPath = "last.windowPlacement";
		if (appSettings.containsPath(windowPath)) {
			scene = new Scene(windowRoot, appSettings.getInt(windowPath + ".size.w"), appSettings.getInt(windowPath + ".size.h"));
			window.setX(appSettings.getInt(windowPath + ".position.x"));
			window.setY(appSettings.getInt(windowPath + ".position.y"));
		} else {
			scene = new Scene(windowRoot, appConfig.getInt("default.windowSize.w"), appConfig.getInt("default.windowSize.h"));
		}
		scene.getStylesheets().add(MeshEditor.class.getResource("/styles/dark.css").toExternalForm());
		window.setScene(scene);
	}

	public AnchorPane getPropertiesViewRoot() {
		return propertiesViewRoot;
	}

	public StackPane getCanvasViewRoot() {
		return canvasViewRoot;
	}

	public MenuBar getMenuViewRoot() {
		return menuViewRoot;
	}
}
