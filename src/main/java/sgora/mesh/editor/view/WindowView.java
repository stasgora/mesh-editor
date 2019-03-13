package sgora.mesh.editor.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sgora.mesh.editor.MeshEditor;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.interfaces.files.WorkspaceAction;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.ui.MainToolbar;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowView {

	public AnchorPane propertiesViewRoot;
	public AnchorPane canvasViewRoot;
	public MenuBar menuViewRoot;

	private AppConfigReader appConfig;
	private SettableObservable<LoadState> loadState;
	private Stage window;

	public SplitPane mainSplitPane;

	public MainToolbar toolBar;

	private WorkspaceAction workspaceAction;

	public void init(SettableObservable<LoadState> loadState, Stage window, AppConfigReader appConfig, WorkspaceAction workspaceAction) {
		this.loadState = loadState;
		this.window = window;
		this.appConfig = appConfig;
		this.workspaceAction = workspaceAction;

		setWindowTitle();
		setListeners();
		window.setOnCloseRequest(workspaceAction::onWindowCloseRequest);
	}

	private void setListeners() {
		LoadState loadState = this.loadState.get();
		loadState.loaded.addListener(this::setWindowTitle);
		loadState.file.addListener(this::setWindowTitle);
		loadState.stateSaved.addListener(this::setWindowTitle);
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
		loadState.loaded.addListener(() -> propertiesViewRoot.setVisible(loadState.loaded.get()));
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
