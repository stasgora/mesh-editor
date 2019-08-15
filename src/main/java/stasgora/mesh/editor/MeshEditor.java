package stasgora.mesh.editor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import stasgora.mesh.editor.model.project.ModelModule;
import stasgora.mesh.editor.services.config.ConfigModule;
import stasgora.mesh.editor.services.drawing.DrawingModule;
import stasgora.mesh.editor.services.files.FileIOModule;
import stasgora.mesh.editor.services.history.ActionHistoryModule;
import stasgora.mesh.editor.services.input.InputModule;
import stasgora.mesh.editor.services.mesh.generation.MeshGenerationModule;
import stasgora.mesh.editor.services.mesh.rendering.MeshRenderingModule;
import stasgora.mesh.editor.view.WindowModule;
import stasgora.mesh.editor.view.WindowView;

public class MeshEditor extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WindowView.fxml"));
		Parent root = loader.load();
		WindowView windowView = loader.getController();

		Injector injector = Guice.createInjector(new WindowModule(root, stage, loader.getNamespace()), new ConfigModule(), new ModelModule(),
				new MeshGenerationModule(), new MeshRenderingModule(), new DrawingModule(), new InputModule(), new ActionHistoryModule(), new FileIOModule());
		injector.injectMembers(windowView);

		new ObjectGraphFactory(windowView, root, stage, loader.getNamespace()).createObjectGraph();
		stage.getIcons().add(new Image(MeshEditor.class.getResourceAsStream("/logo.png")));
		stage.requestFocus();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
