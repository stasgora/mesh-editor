package sgora.mesh.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sgora.mesh.editor.view.Window;

public class MeshEditor extends Application {

	@Override
	public void start(Stage stage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
		Parent root = loader.load();
		stage.setTitle("Mesh Editor");
		stage.setScene(new Scene(root, 1200, 800));
		stage.requestFocus();
		stage.show();
		((Window) loader.getController()).init(stage);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
