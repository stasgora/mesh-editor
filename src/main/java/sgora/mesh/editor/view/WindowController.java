package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sgora.mesh.editor.enums.FileChooserAction;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.interfaces.config.LangConfigReader;
import sgora.mesh.editor.model.JsonConfig;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.services.UiDialogUtils;
import sgora.mesh.editor.services.files.WorkspaceActionHandler;
import sgora.mesh.editor.ui.*;
import sgora.mesh.editor.ui.canvas.ImageCanvas;
import sgora.mesh.editor.ui.canvas.MeshCanvas;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class WindowController {

	private AppConfigReader appConfig;
	private SettableObservable<LoadState> loadState;
	private Stage window;

	public SplitPane mainSplitPane;
	public MainView mainView;
	public AnchorPane propertiesPane;

	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;
	public MainToolbar toolBar;

	public MenuItem openRecentMenuItem;

	private WorkspaceActionHandler workspaceActionHandler;
	private UiDialogUtils dialogUtils;
	private ObservableMap<String, Object> fxmlNamespace;
	private LangConfigReader appLang;

	public void init(SettableObservable<LoadState> loadState, Stage window, AppConfigReader appConfig, WorkspaceActionHandler workspaceActionHandler,
	                 UiDialogUtils dialogUtils, ObservableMap<String, Object> fxmlNamespace, LangConfigReader appLang) {
		this.loadState = loadState;
		this.window = window;
		this.appConfig = appConfig;
		this.workspaceActionHandler = workspaceActionHandler;
		this.dialogUtils = dialogUtils;
		this.fxmlNamespace = fxmlNamespace;
		this.appLang = appLang;
		fxmlNamespace.put("menu_file_item_disabled", true);

		setWindowTitle();
		setListeners();
		window.setOnCloseRequest(this::onWindowCloseRequest);
	}

	private void setListeners() {
		LoadState loadState = this.loadState.get();
		loadState.loaded.addListener(() -> fxmlNamespace.put("menu_file_item_disabled", !((boolean) fxmlNamespace.get("menu_file_item_disabled"))));

		loadState.file.addListener(this::setWindowTitle);
		loadState.stateSaved.addListener(this::setWindowTitle);
		loadState.addListener(this::setWindowTitle);

		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
	}

	private boolean showConfirmDialog() {
		return appConfig.getBool("flags.showConfirmDialogs");
	}

	private void setWindowTitle() {
		String title = appConfig.getString("appName");
		LoadState loadState = this.loadState.get();
		if(loadState.loaded.get()) {
			String projectName = getProjectName();
			if(!loadState.stateSaved.get()) {
				projectName += "*";
			}
			title = projectName + " - " + title;
		}
		window.setTitle(title);
	}

	private String getProjectName() {
		String projectName;
		LoadState loadState = this.loadState.get();
		if(loadState.file.get() == null) {
			projectName = loadState.loaded.get() ? appLang.getText("defaultProjectName") : null;
		} else {
			String fileName = loadState.file.get().getName();
			projectName = fileName.substring(0, fileName.length() - appConfig.getString("extension.project").length() - 1);
		}
		return projectName;
	}

	private void keepDividerInPlace(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
		SplitPane.Divider divider = mainSplitPane.getDividers().get(0);
		divider.setPosition(divider.getPosition() * oldVal.doubleValue() / newVal.doubleValue());
	}

	private File showProjectFileChooser(FileChooserAction action) {
		String projectExtension = appConfig.getString("extension.project");
		String extensionTitle = appLang.getText("dialog.fileChooser.extension.project");
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(extensionTitle, "*." + projectExtension);
		return dialogUtils.showFileChooser(action, appLang.getText("dialog.fileChooser.title.project"), filter);
	}

	private void saveProject(boolean asNew) {
		File location;
		File file = this.loadState.get().file.get();
		if(asNew || file == null) {
			location = showProjectFileChooser(FileChooserAction.SAVE_DIALOG);
			if(location == null) {
				return;
			}
		} else {
			location = file;
		}
		workspaceActionHandler.saveProject(location);
	}

	private void saveProject() {
		saveProject(false);
	}

	private boolean confirmWorkspaceAction(String title) {
		if(loadState.get().stateSaved.get()) {
			return true;
		}
		ButtonType saveButton = new ButtonType(appLang.getText("action.save"));
		ButtonType discardButton = new ButtonType(appLang.getText("action.discard"));
		ButtonType cancelButton = new ButtonType(appLang.getText("action.cancel"));
		List<String> headerParts = appLang.getMultipartText("dialog.warning.header.modified");
		String headerText = headerParts.get(0) + getProjectName() + headerParts.get(1);
		String contentText = appLang.getText("dialog.warning.content.modified");
		ButtonType[] buttonTypes = {saveButton, discardButton, cancelButton};
		Optional<ButtonType> response = dialogUtils.showWarningDialog(title, headerText, contentText, buttonTypes);
		if(!response.isPresent() || response.get() == cancelButton) {
			return false;
		}
		if(response.get() == saveButton) {
			saveProject();
		}
		return true;
	}

	public void newProject(ActionEvent event) {
		if(showConfirmDialog() && !confirmWorkspaceAction(appLang.getText("action.createProject"))) {
			return;
		}
		String[] imageTypes = appConfig.getStringList("supported.imageTypes").stream().map(item -> "*." + item).toArray(String[]::new);
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(appLang.getText("dialog.fileChooser.extension.image"), imageTypes);
		File location = dialogUtils.showFileChooser(FileChooserAction.OPEN_DIALOG, appLang.getText("dialog.fileChooser.title.image"), filter);
		if(location != null) {
			workspaceActionHandler.createNewProject(location);
		}
	}

	public void openProject(ActionEvent event) {
		if(showConfirmDialog() && !confirmWorkspaceAction(appLang.getText("action.openProject"))) {
			return;
		}
		File location = showProjectFileChooser(FileChooserAction.OPEN_DIALOG);
		if(location != null) {
			workspaceActionHandler.openProject(location);
		}
	}

	public void openRecentProject(ActionEvent event) {
	}

	public void closeProject(ActionEvent event) {
		if(!showConfirmDialog() || confirmWorkspaceAction(appLang.getText("action.closeProject"))) {
			workspaceActionHandler.closeProject();
		}
	}

	public void saveProject(ActionEvent event) {
		saveProject();
	}

	public void saveProjectAs(ActionEvent event) {
		saveProject(true);
	}

	private void onWindowCloseRequest(WindowEvent event) {
		if(showConfirmDialog() && !confirmWorkspaceAction(appLang.getText("action.quit"))) {
			event.consume();
		}
	}

	public void exitApp(ActionEvent event) {
		if(!showConfirmDialog() || confirmWorkspaceAction(appLang.getText("action.quit"))) {
			Platform.exit();
		}
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
