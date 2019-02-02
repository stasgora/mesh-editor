package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.enums.ProjectFileAction;
import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.services.ProjectFileUtils;
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

	private FileUtils fileUtils = new ProjectFileUtils();

	private final static String APP_NAME = "Mesh Editor";

	public void init(Stage stage) {
		this.stage = stage;
		toolBar.init(model.activeTool);
		mainView.init(model, imageCanvas, meshCanvas);

		setWindowTitle();
		model.project.loaded.addListener(this::onProjectLoadedChange);
		model.project.addListener(this::setWindowTitle);
		model.project.baseImage.addListener(this::onProjectChanged);
		model.project.name.addListener(this::setWindowTitle);
		model.project.stateSaved.addListener(this::setWindowTitle);

		model.mouseCursor = stage.getScene().cursorProperty();
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);

		onProjectLoadedChange();
	}

	private void setWindowTitle() {
		String title = APP_NAME;
		if(model.project.loaded.get() && model.project.name.get() != null && !model.project.name.get().isEmpty()) {
			String projectName = model.project.name.get();
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

	private File showProjectFileChooser(ProjectFileAction action) {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle("Choose Project File");
		projectFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Project Files", "*." + ProjectFileUtils.PROJECT_FILE_EXTENSION));
		if(action == ProjectFileAction.SAVE)
			return projectFileChooser.showSaveDialog(stage);
		else if(action == ProjectFileAction.OPEN)
			return projectFileChooser.showOpenDialog(stage);
		return null;
	}

	private void onProjectLoadedChange() {
		boolean loaded = model.project.loaded.get();
		closeProjectMenuItem.setDisable(!loaded);
		saveProjectMenuItem.setDisable(!loaded);
		saveAsMenuItem.setDisable(!loaded);
	}

	private void onProjectChanged() {
		if(model.project.loaded.get())
			model.project.stateSaved.set(true);
		fileUtils.setProjectFileName(model.project);
	}

	private void saveNewProject() {
		File location = showProjectFileChooser(ProjectFileAction.SAVE);
		if(location == null)
			return;
		location = fileUtils.getProjectFileWithExtension(location);
		saveProjectToFile(location);
		model.project.file.set(location);
		fileUtils.setProjectFileName(model.project);
	}

	private void saveProjectToFile(File location) {
		try {
			fileUtils.save(model.project, location);
		} catch (ProjectIOException e) {
		}
	}

	public void newProject(ActionEvent event) {
		File image = chooseBaseImage();
		if(image == null)
			return;
		try(FileInputStream fileStream = new FileInputStream(image)) {
			fileUtils.loadImage(model.project, fileStream);
			model.project.mesh.set(new Mesh());
			model.project.loaded.set(true);
			model.project.file.set(null);
			fileUtils.setProjectFileName(model.project);
			model.project.notifyListeners();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void openProject(ActionEvent event) {
		File location = showProjectFileChooser(ProjectFileAction.OPEN);
		if(location == null)
			return;
		try {
			fileUtils.load(model.project, location);
			mainView.imageBox.calcImageBox();
			model.project.loaded.set(true);
			model.project.file.set(location);
			fileUtils.setProjectFileName(model.project);
			model.project.notifyListeners();
		} catch (ProjectIOException e) {
		}
	}

	public void openRecentProject(ActionEvent event) {

	}

	public void closeProject(ActionEvent event) {
		model.project.mesh.set(null);
		model.project.baseImage.set(null);
		model.project.loaded.set(false);
		model.project.file.set(null);
		fileUtils.setProjectFileName(model.project);
		model.project.notifyListeners();
	}

	public void saveProject(ActionEvent event) {
		if(model.project.file.get() == null)
			saveNewProject();
		else
			saveProjectToFile(model.project.file.get());
		model.project.stateSaved.set(true);
	}

	public void saveProjectAs(ActionEvent event) {
		saveNewProject();
		model.project.stateSaved.set(true);
	}

	public void exitApp(ActionEvent event) {
		Platform.exit();
	}

}
