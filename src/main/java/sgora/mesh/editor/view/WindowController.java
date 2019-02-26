package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sgora.mesh.editor.enums.FileChooserAction;
import sgora.mesh.editor.interfaces.AppConfigReader;
import sgora.mesh.editor.interfaces.LangConfigReader;
import sgora.mesh.editor.model.Project;
import sgora.mesh.editor.services.UiDialogUtils;
import sgora.mesh.editor.services.files.WorkspaceActionHandler;
import sgora.mesh.editor.ui.*;

import java.io.*;
import java.util.List;
import java.util.Optional;

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

	public MenuItem openRecentMenuItem;

	private WorkspaceActionHandler workspaceActionHandler;
	private UiDialogUtils dialogUtils;
	private ObservableMap<String, Object> fxmlNamespace;
	private LangConfigReader appLang;

	public void init(Project project, Stage window, AppConfigReader appConfig, WorkspaceActionHandler workspaceActionHandler,
	                 UiDialogUtils dialogUtils, ObservableMap<String, Object> fxmlNamespace, LangConfigReader appLang) {
		this.project = project;
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
		project.loaded.addListener(() -> fxmlNamespace.put("menu_file_item_disabled", !((boolean) fxmlNamespace.get("menu_file_item_disabled"))));

		project.file.addListener(this::setWindowTitle);
		project.stateSaved.addListener(this::setWindowTitle);
		project.addListener(this::setWindowTitle);

		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
	}

	private boolean showConfirmDialog() {
		return appConfig.getBool("flags.showConfirmDialogs");
	}

	private void setWindowTitle() {
		String title = appConfig.getString("appName");
		if(project.loaded.get()) {
			String projectName = getProjectName();
			if(!project.stateSaved.get()) {
				projectName += "*";
			}
			title = projectName + " - " + title;
		}
		window.setTitle(title);
	}

	private String getProjectName() {
		String projectName;
		if(project.file.get() == null) {
			projectName = project.loaded.get() ? appLang.getText("defaultProjectName") : null;
		} else {
			String fileName = project.file.get().getName();
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
		if(asNew || project.file.get() == null) {
			location = showProjectFileChooser(FileChooserAction.SAVE_DIALOG);
			if(location == null) {
				return;
			}
		} else {
			location = project.file.get();
		}
		workspaceActionHandler.saveProject(location);
	}

	private void saveProject() {
		saveProject(false);
	}

	private boolean confirmWorkspaceAction(String title) {
		if(project.stateSaved.get()) {
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

}
