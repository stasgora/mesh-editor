package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.enums.WorkspaceAction;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.services.ProjectFileUtils;
import sgora.mesh.editor.services.WorkspaceActionHandler;
import sgora.mesh.editor.ui.*;

import java.io.*;

public class Window {

	private Stage stage;

	private Model model = new Model();

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

	private final static String APP_NAME = "Mesh Editor";

	public void init(Stage stage) {
		this.stage = stage;
		toolBar.init(model.activeTool);
		mainView.init(model, imageCanvas, meshCanvas);
		workspaceActionHandler = new WorkspaceActionHandler(model.project);

		setWindowTitle();
		model.project.loaded.addListener(this::changeMenuItemState);

		model.project.file.addListener(this::setWindowTitle);
		model.project.stateSaved.addListener(this::setWindowTitle);
		model.project.addListener(this::setWindowTitle);

		model.mouseCursor = stage.getScene().cursorProperty();
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
	}

	private void setWindowTitle() {
		String title = APP_NAME;
		if(model.project.loaded.get()) {
			String projectName;
			if(model.project.file.get() == null) {
				projectName = model.project.loaded.get() ? ProjectFileUtils.DEFAULT_PROJECT_FILE_NAME : null;
			} else {
				String fileName = model.project.file.get().getName();
				projectName = fileName.substring(0, fileName.length() - ProjectFileUtils.PROJECT_FILE_EXTENSION.length() - 1);
			}
			if(!model.project.stateSaved.get())
				projectName += "*";
			title = projectName + " - " + title;
		}
		stage.setTitle(title);
	}

	private void keepDividerInPlace(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
		SplitPane.Divider divider = mainSplitPane.getDividers().get(0);
		divider.setPosition(divider.getPosition() * oldVal.doubleValue() / newVal.doubleValue());
	}

	private File chooseBaseImage() {
		FileChooser imageChooser = new FileChooser();
		imageChooser.setTitle("Choose Image");
		imageChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
		return imageChooser.showOpenDialog(stage);
	}

	private File showProjectFileChooser(WorkspaceAction action) {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle("Choose Project File");
		projectFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Project Files", "*." + ProjectFileUtils.PROJECT_FILE_EXTENSION));
		if(action == WorkspaceAction.SAVE_PROJECT)
			return projectFileChooser.showSaveDialog(stage);
		else if(action == WorkspaceAction.OPEN_PROJECT)
			return projectFileChooser.showOpenDialog(stage);
		return null;
	}

	private void changeMenuItemState() {
		boolean loaded = model.project.loaded.get();
		closeProjectMenuItem.setDisable(!loaded);
		saveProjectMenuItem.setDisable(!loaded);
		saveAsMenuItem.setDisable(!loaded);
	}

	public void newProject(ActionEvent event) {
		File location = chooseBaseImage();
		if(location == null)
			return;
		workspaceActionHandler.createNewProject(location);
	}

	public void openProject(ActionEvent event) {
		File location = showProjectFileChooser(WorkspaceAction.OPEN_PROJECT);
		if(location == null)
			return;
		workspaceActionHandler.openProject(location);
	}

	public void openRecentProject(ActionEvent event) {

	}

	public void closeProject(ActionEvent event) {
		workspaceActionHandler.closeProject();
	}

	public void saveProject(ActionEvent event) {
		File location;
		if(model.project.file.get() == null) {
			location = showProjectFileChooser(WorkspaceAction.SAVE_PROJECT);
			if(location == null)
				return;
		} else
			location = model.project.file.get();
		workspaceActionHandler.saveProject(location);
	}

	public void saveProjectAs(ActionEvent event) {
		File location = showProjectFileChooser(WorkspaceAction.SAVE_PROJECT);
		if(location == null)
			return;
		workspaceActionHandler.saveProject(location);
	}

	public void exitApp(ActionEvent event) {
		Platform.exit();
	}

}
