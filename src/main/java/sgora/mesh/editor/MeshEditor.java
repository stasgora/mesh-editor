package sgora.mesh.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sgora.mesh.editor.view.WindowController;

public class MeshEditor extends Application {

	private State state = new State();

	@Override
	public void start(Stage stage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
		Parent root = loader.load();

		stage.setScene(new Scene(root, 1200, 800));
		stage.requestFocus();
		stage.show();
		((WindowController) loader.getController()).init(state, stage);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
