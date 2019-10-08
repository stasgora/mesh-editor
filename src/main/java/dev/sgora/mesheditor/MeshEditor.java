package dev.sgora.mesheditor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.sgora.mesheditor.services.files.FileIOModule;
import dev.sgora.mesheditor.services.files.workspace.WorkspaceActionModule;
import dev.sgora.mesheditor.services.history.ActionHistoryModule;
import dev.sgora.mesheditor.services.mesh.generation.MeshGenerationModule;
import dev.sgora.mesheditor.services.mesh.rendering.MeshRenderingModule;
import dev.sgora.mesheditor.services.ui.UIModule;
import dev.sgora.mesheditor.view.ViewType;
import dev.sgora.mesheditor.view.WindowModule;
import dev.sgora.mesheditor.view.WindowView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import dev.sgora.mesheditor.model.project.ModelModule;
import dev.sgora.mesheditor.services.config.ConfigModule;
import dev.sgora.mesheditor.services.config.LangConfigReader;
import dev.sgora.mesheditor.services.drawing.DrawingModule;
import dev.sgora.mesheditor.services.files.workspace.interfaces.WorkspaceAction;
import dev.sgora.mesheditor.services.input.InputModule;
import dev.sgora.mesheditor.view.sub.SubViewFactory;

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
		subViewFactory.buildPropertiesView(windowView.getPropertiesViewRoot(), ViewType.PROPERTIES_VIEW);
		subViewFactory.buildMenuView(windowView.getMenuViewRoot(), ViewType.MENU_VIEW);
		subViewFactory.buildCanvasView(windowView.getCanvasViewRoot(), ViewType.CANVAS_VIEW);

		injector.getInstance(LangConfigReader.class).onSetMainLanguage(); //TODO move once we have language settings
	}

	public static void main(String[] args) {
		launch(args);
	}

}
