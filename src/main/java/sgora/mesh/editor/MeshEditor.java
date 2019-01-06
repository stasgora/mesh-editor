package sgora.mesh.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sgora.mesh.editor.controller.AppController;

public class MeshEditor extends Application {

	@Override
	public void start(Stage stage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
		Parent root = loader.load();
		((AppController) loader.getController()).init(stage);
		stage.setTitle("Mesh Editor");
		stage.setScene(new Scene(root, 1200, 800));

		stage.requestFocus();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
