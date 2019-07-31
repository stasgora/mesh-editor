package stasgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.files.workspace.WorkspaceAction;
import stasgora.mesh.editor.model.project.LoadState;

public class WindowView {

	public AnchorPane propertiesViewRoot;
	public AnchorPane canvasViewRoot;
	public MenuBar menuViewRoot;

	private AppConfigReader appConfig;
	private LoadState loadState;
	private Stage window;

	public SplitPane mainSplitPane;

	private WorkspaceAction workspaceAction;

	public void init(LoadState loadState, Stage window, AppConfigReader appConfig, WorkspaceAction workspaceAction) {
		this.loadState = loadState;
		this.window = window;
		this.appConfig = appConfig;
		this.workspaceAction = workspaceAction;

		setWindowTitle();
		setListeners();
		window.setOnCloseRequest(workspaceAction::onWindowCloseRequest);
	}

	private void setListeners() {
		LoadState loadState = this.loadState;
		loadState.addListener(this::setWindowTitle);
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
		loadState.loaded.addListener(() -> propertiesViewRoot.setVisible(loadState.loaded.get()));
	}

	private void setWindowTitle() {
		String title = appConfig.getString("appName");
		LoadState loadState = this.loadState;
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

	public void createWindowScene(AppConfigReader appSettings, Stage stage, Parent root) {
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
