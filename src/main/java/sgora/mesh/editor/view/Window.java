package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.ui.*;

import java.io.File;

public class Window {
	private Stage stage;

	// Model
	private Model model = new Model();

	// UI
	public MainView mainView;
	public ImageCanvas imageCanvas;
	public MeshCanvas meshCanvas;
	public MainToolBar toolBar;

	public void init(Stage stage) {
		this.stage = stage;
		toolBar.init(model.activeTool);
		mainView.init(model, imageCanvas, meshCanvas);
	}

	public void loadImage(ActionEvent event) {
		FileChooser imageChooser = new FileChooser();
		imageChooser.setTitle("Choose Image");
		imageChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.bmp"));
		File image = imageChooser.showOpenDialog(stage);

		if(image == null)
			return;
		mainView.imageBox.setBaseImage(image.getAbsolutePath());
	}

	public void exitApp(ActionEvent event) {
		Platform.exit();
	}
}
