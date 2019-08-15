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
import stasgora.mesh.editor.services.config.LangConfigReader;
import stasgora.mesh.editor.services.drawing.DrawingModule;
import stasgora.mesh.editor.services.files.FileIOModule;
import stasgora.mesh.editor.services.files.workspace.WorkspaceAction;
import stasgora.mesh.editor.services.files.workspace.WorkspaceActionModule;
import stasgora.mesh.editor.services.history.ActionHistoryModule;
import stasgora.mesh.editor.services.input.InputModule;
import stasgora.mesh.editor.services.mesh.generation.MeshGenerationModule;
import stasgora.mesh.editor.services.mesh.rendering.MeshRenderingModule;
import stasgora.mesh.editor.services.ui.UIModule;
import stasgora.mesh.editor.view.*;
import stasgora.mesh.editor.view.sub.SubViewFactory;

public class MeshEditor extends Application {
	private WindowView windowView;
	private Injector injector;

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WindowView.fxml"));
		Parent root = loader.load();
		windowView = loader.getController();

		injector = Guice.createInjector(new WindowModule(root, stage, loader.getNamespace()), new ConfigModule(),
				new ModelModule(), new MeshGenerationModule(), new MeshRenderingModule(), new DrawingModule(), new InputModule(),
				new ActionHistoryModule(), new FileIOModule(), new WorkspaceActionModule(), new UIModule());
		initViews();

		stage.getIcons().add(new Image(MeshEditor.class.getResourceAsStream("/logo.png")));
		stage.requestFocus();
		stage.show();
	}

	private void initViews() {
		injector.injectMembers(windowView);
		windowView.setWorkspaceAction(injector.getInstance(WorkspaceAction.class));

		SubViewFactory subViewFactory = injector.getInstance(SubViewFactory.class);
		subViewFactory.buildPropertiesView(windowView.propertiesViewRoot, ViewType.PROPERTIES_VIEW);
		subViewFactory.buildMenuView(windowView.menuViewRoot, ViewType.MENU_VIEW);
		subViewFactory.buildCanvasView(windowView.canvasViewRoot, ViewType.CANVAS_VIEW);

		injector.getInstance(LangConfigReader.class).onSetMainLanguage(); //TODO move once we have language settings
	}

	public static void main(String[] args) {
		launch(args);
	}

}
