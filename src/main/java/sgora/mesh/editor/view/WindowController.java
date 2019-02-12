package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sgora.mesh.editor.State;
import sgora.mesh.editor.enums.FileChooserAction;
import sgora.mesh.editor.model.containers.ProjectModel;
import sgora.mesh.editor.services.ProjectFileUtils;
import sgora.mesh.editor.services.UiDialogUtils;
import sgora.mesh.editor.services.WorkspaceActionHandler;
import sgora.mesh.editor.ui.*;

import java.io.*;
import java.util.Optional;

public class WindowController {

	private State state;
	private Stage window;

	public SplitPane mainSplitPane;
	public MainView mainView;
	public AnchorPane propertiesPane;

	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;
	public MainToolBar toolBar;

	public MenuItem openRecentMenuItem;
	public MenuItem closeProjectMenuItem;
	public MenuItem saveProjectMenuItem;
	public MenuItem saveAsMenuItem;

	private WorkspaceActionHandler workspaceActionHandler;
	private UiDialogUtils dialogUtils;

	private final static String APP_NAME = "Mesh Editor";

	public void init(State state, Stage window) {
		this.state = state;
		this.window = window;
		toolBar.init(state.model.activeTool);
		mainView.init(state, imageCanvas, meshCanvas);
		workspaceActionHandler = new WorkspaceActionHandler(state);
		dialogUtils = new UiDialogUtils(window);

		setWindowTitle();
		project().loaded.addListener(this::changeMenuItemState);

		project().file.addListener(this::setWindowTitle);
		project().stateSaved.addListener(this::setWindowTitle);
		project().addListener(this::setWindowTitle);

		state.model.mouseCursor = window.getScene().cursorProperty();
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);

		window.setOnCloseRequest(this::onWindowCloseRequest);
	}

	private ProjectModel project() {
		return state.model.project;
	}

	private boolean showConfirmDialog() {
		return state.config.appConfig.<Boolean>getValue("flags.showConfirmDialogs");
	}

	private void setWindowTitle() {
		String title = APP_NAME;
		if(project().loaded.get()) {
			String projectName = getProjectName();
			if(!project().stateSaved.get())
				projectName += "*";
			title = projectName + " - " + title;
		}
		window.setTitle(title);
	}

	private String getProjectName() {
		String projectName;
		if(project().file.get() == null) {
			projectName = project().loaded.get() ? ProjectFileUtils.DEFAULT_PROJECT_FILE_NAME : null;
		} else {
			String fileName = project().file.get().getName();
			projectName = fileName.substring(0, fileName.length() - state.config.appConfig.<String>getValue("projectExtension").length() - 1);
		}
		return projectName;
	}

	private void keepDividerInPlace(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
		SplitPane.Divider divider = mainSplitPane.getDividers().get(0);
		divider.setPosition(divider.getPosition() * oldVal.doubleValue() / newVal.doubleValue());
	}

	private void changeMenuItemState() {
		boolean loaded = project().loaded.get();
		closeProjectMenuItem.setDisable(!loaded);
		saveProjectMenuItem.setDisable(!loaded);
		saveAsMenuItem.setDisable(!loaded);
	}

	private File showProjectFileChooser(FileChooserAction action) {
		String projectExtension = state.config.appConfig.<String>getValue("projectExtension");
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Project Files", "*." + projectExtension);
		return dialogUtils.showFileChooser(action, "Choose Project File", filter);
	}

	private void saveProject(boolean asNew) {
		File location;
		if(asNew || project().file.get() == null) {
			location = showProjectFileChooser(FileChooserAction.SAVE_DIALOG);
			if(location == null)
				return;
		} else
			location = project().file.get();
		workspaceActionHandler.saveProject(location);
	}

	private void saveProject() {
		saveProject(false);
	}

	private boolean confirmWorkspaceAction(String title) {
		if(project().stateSaved.get())
			return true;
		ButtonType saveButton = new ButtonType("Save");
		ButtonType discardButton = new ButtonType("Discard");
		ButtonType cancelButton = new ButtonType("Cancel");
		String headerText = "Currently open project \"" + getProjectName() + "\" has been modified";
		String contentText = "Do you want to save your changes or discard them?";
		ButtonType[] buttonTypes = {saveButton, discardButton, cancelButton};
		Optional<ButtonType> response = dialogUtils.showWarningDialog(title, headerText, contentText, buttonTypes);
		if(!response.isPresent() || response.get() == cancelButton)
			return false;
		if(response.get() == saveButton)
			saveProject();
		return true;
	}

	public void newProject(ActionEvent event) {
		if(showConfirmDialog() && !confirmWorkspaceAction("Create Project"))
			return;
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp");
		File location = dialogUtils.showFileChooser(FileChooserAction.OPEN_DIALOG, "Choose Image", filter);
		if(location != null)
			workspaceActionHandler.createNewProject(location);
	}

	public void openProject(ActionEvent event) {
		if(showConfirmDialog() && !confirmWorkspaceAction("Open Project"))
			return;
		File location = showProjectFileChooser(FileChooserAction.OPEN_DIALOG);
		if(location != null)
			workspaceActionHandler.openProject(location);
	}

	public void openRecentProject(ActionEvent event) {
	}

	public void closeProject(ActionEvent event) {
		if(!showConfirmDialog() || confirmWorkspaceAction("Close Project"))
			workspaceActionHandler.closeProject();
	}

	public void saveProject(ActionEvent event) {
		saveProject();
	}

	public void saveProjectAs(ActionEvent event) {
		saveProject(true);
	}

	private void onWindowCloseRequest(WindowEvent event) {
		if(showConfirmDialog() && !confirmWorkspaceAction("Exit"))
			event.consume();
	}

	public void exitApp(ActionEvent event) {
		if(!showConfirmDialog() || confirmWorkspaceAction("Exit"))
			Platform.exit();

	}

}
