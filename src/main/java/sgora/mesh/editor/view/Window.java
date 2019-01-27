package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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

	private ProjectWriter projectWriter = new ProjectFileWriter();

	private final static String APP_NAME = "Mesh Editor";

	public void init(Stage stage) {
		this.stage = stage;
		toolBar.init(model.activeTool);
		mainView.init(model, imageCanvas, meshCanvas);

		setWindowTitle();
		model.projectLoaded.addListener(this::setWindowTitle);
		model.projectName.addListener(this::setWindowTitle);

		model.mouseCursor = stage.getScene().cursorProperty();
		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
	}

	private void setWindowTitle() {
		String title = APP_NAME;
		if(model.projectLoaded.get() && model.projectName.get() != null && !model.projectName.get().isEmpty()) {
			title = model.projectName.get() + " - " + title;
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

	public void newProject(ActionEvent event) {
		String imagePath = chooseBaseImage();
		if(imagePath == null)
			return;
		model.projectLoaded.set(true);
		mainView.imageBox.setBaseImage(imagePath);
	}

	public void openProject(ActionEvent event) {

	}

	public void openRecentProject(ActionEvent event) {

	}

	public void closeProject(ActionEvent event) {
		model.projectLoaded.set(false);
	}

	private File chooseProjectFileLocation() {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle("Choose Project File");
		return projectFileChooser.showSaveDialog(stage);
	}

	public void saveProject(ActionEvent event) {
		if(model.projectFile == null)
			saveNewProject();
		else
			projectWriter.saveProject(model);
	}

	private void saveNewProject() {
		File newProjectFile = chooseProjectFileLocation();
		if(newProjectFile == null)
			return;
		projectWriter.saveProject(model);
		model.projectFile = newProjectFile;
		model.projectName.set(newProjectFile.getName());
	}

	public void saveProjectAs(ActionEvent event) {
		saveNewProject();
	}

}
