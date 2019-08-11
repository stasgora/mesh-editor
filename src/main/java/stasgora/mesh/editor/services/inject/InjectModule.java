package stasgora.mesh.editor.services.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javafx.collections.ObservableMap;
import javafx.scene.Parent;
import javafx.stage.Stage;
import stasgora.mesh.editor.model.NamespaceMap;
import stasgora.mesh.editor.view.ViewType;
import stasgora.mesh.editor.view.WindowView;

public class InjectModule extends AbstractModule {
	private final WindowView windowView;
	private final Parent root;
	private final Stage stage;
	private final NamespaceMap namespaceMap = new NamespaceMap();

	public InjectModule(WindowView windowView, Parent root, Stage stage, ObservableMap<String, Object> windowNamespace) {
		this.windowView = windowView;
		this.root = root;
		this.stage = stage;

		namespaceMap.put(ViewType.WINDOW_VIEW.langPrefix, windowNamespace);
	}

	@Provides @Singleton
	NamespaceMap namespaceMap() {
		return namespaceMap;
	}
}
