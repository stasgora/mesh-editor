package sgora.mesh.editor.view;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.model.containers.Model;
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


	public void init(Stage stage) {
		this.stage = stage;
		toolBar.init(model.activeTool);
		mainView.init(model, imageCanvas, meshCanvas);

		mainSplitPane.widthProperty().addListener(this::keepDividerInPlace);
	}

	private void keepDividerInPlace(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
		SplitPane.Divider divider = mainSplitPane.getDividers().get(0);
		divider.setPosition(divider.getPosition() * oldVal.doubleValue() / newVal.doubleValue());
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
