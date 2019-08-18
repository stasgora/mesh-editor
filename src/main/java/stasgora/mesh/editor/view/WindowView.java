package stasgora.mesh.editor.view;

import com.google.inject.Inject;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import stasgora.mesh.editor.MeshEditor;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.files.workspace.WorkspaceAction;
import stasgora.mesh.editor.view.annotation.MainWindowRoot;
import stasgora.mesh.editor.view.annotation.MainWindowStage;

public class WindowView {

	public AnchorPane propertiesViewRoot;
	public AnchorPane canvasViewRoot;
	public MenuBar menuViewRoot;

	private AppConfigReader appConfig;
	private LoadState loadState;
	private Stage window;

	public SplitPane mainSplitPane;

	private WorkspaceAction workspaceAction;

	@Inject
	void init(LoadState loadState, @MainWindowStage Stage window, @AppConfig AppConfigReader appConfig,
	          @AppConfig AppConfigReader appSettings, @MainWindowRoot Parent windowRoot) {
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
		LoadState loadState = this.loadState;
		loadState.addListener(this::setWindowTitle);
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
		loadState.loaded.addListener(() -> propertiesViewRoot.setVisible(loadState.loaded.get()));
	}

	private void setWindowTitle() {
		String title = appConfig.getString("app.name");
		LoadState loadState = this.loadState;
		if (loadState.loaded.get()) {
			String projectName = workspaceAction.getProjectName();
			if (!loadState.stateSaved.get()) {
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

}
