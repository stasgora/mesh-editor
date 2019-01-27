package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.interfaces.ProjectWriter;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.services.ProjectFileWriter;
import sgora.mesh.editor.ui.*;

import java.io.File;

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

	private ProjectWriter projectWriter = new ProjectFileWriter();

	private final static String APP_NAME = "Mesh Editor";

	public void init(Stage stage) {
		this.stage = stage;
		toolBar.init(model.activeTool);
		mainView.init(model, imageCanvas, meshCanvas);

		setWindowTitle();
		model.project.loaded.addListener(this::setWindowTitle);
		model.project.name.addListener(this::setWindowTitle);

		model.mouseCursor = stage.getScene().cursorProperty();
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);

		onProjectUnloaded();
	}

	private void setWindowTitle() {
		String title = APP_NAME;
		if(model.project.loaded.get() && model.project.name.get() != null && !model.project.name.get().isEmpty()) {
			title = model.project.name.get() + " - " + title;
		}
		stage.setTitle(title);
	}

	private void keepDividerInPlace(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
		SplitPane.Divider divider = mainSplitPane.getDividers().get(0);
		divider.setPosition(divider.getPosition() * oldVal.doubleValue() / newVal.doubleValue());
	}

	private String chooseBaseImage() {
		FileChooser imageChooser = new FileChooser();
		imageChooser.setTitle("Choose Image");
		imageChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
		File image = imageChooser.showOpenDialog(stage);

		if(image == null)
			return null;
		return image.getAbsolutePath();
	}

	public void exitApp(ActionEvent event) {
		Platform.exit();
	}

	private void onProjectLoaded() {
		model.project.loaded.set(true);
		closeProjectMenuItem.setDisable(false);
		saveProjectMenuItem.setDisable(false);
		saveAsMenuItem.setDisable(false);
	}

	private void onProjectUnloaded() {
		model.project.loaded.set(false);
		closeProjectMenuItem.setDisable(true);
		saveProjectMenuItem.setDisable(true);
		saveAsMenuItem.setDisable(true);
	}

	public void newProject(ActionEvent event) {
		String imagePath = chooseBaseImage();
		if(imagePath == null)
			return;
		onProjectLoaded();
		mainView.imageBox.setBaseImage(imagePath);
	}

	public void openProject(ActionEvent event) {

	}

	public void openRecentProject(ActionEvent event) {

	}

	public void closeProject(ActionEvent event) {
		onProjectUnloaded();
	}

	private File chooseProjectFileLocation() {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle("Choose Project File");
		return projectFileChooser.showSaveDialog(stage);
	}

	public void saveProject(ActionEvent event) {
		if(model.project.file == null)
			saveNewProject();
		else
			projectWriter.saveProject(model);
	}

	private void saveNewProject() {
		File newProjectFile = chooseProjectFileLocation();
		if(newProjectFile == null)
			return;
		projectWriter.saveProject(model);
		model.project.file = newProjectFile;
		model.project.name.set(newProjectFile.getName());
	}

	public void saveProjectAs(ActionEvent event) {
		saveNewProject();
	}

}
