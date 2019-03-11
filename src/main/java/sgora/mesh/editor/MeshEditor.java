package sgora.mesh.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import sgora.mesh.editor.view.WindowController;

public class MeshEditor extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
		Parent root = loader.load();
		WindowController controller = loader.getController();

		new ObjectGraphFactory(controller, root, stage, loader).createObjectGraph();
		stage.requestFocus();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
