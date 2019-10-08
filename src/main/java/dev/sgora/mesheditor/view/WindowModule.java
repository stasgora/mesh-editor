package dev.sgora.mesheditor.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import dev.sgora.mesheditor.model.NamespaceMap;
import dev.sgora.mesheditor.view.annotation.MainWindowRoot;
import dev.sgora.mesheditor.view.annotation.MainWindowStage;
import dev.sgora.mesheditor.view.sub.SubView;
import dev.sgora.mesheditor.view.sub.SubViewFactory;
import javafx.collections.ObservableMap;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class WindowModule extends AbstractModule {
	private final Parent mainWindowRoot;
	private final Stage mainWindowStage;
	private final NamespaceMap namespaceMap = new NamespaceMap();

	public WindowModule(Parent mainWindowRoot, Stage mainWindowStage, ObservableMap<String, Object> windowNamespace) {
		this.mainWindowRoot = mainWindowRoot;
		this.mainWindowStage = mainWindowStage;

		namespaceMap.put(ViewType.WINDOW_VIEW.langPrefix, windowNamespace);
	}

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(SubView.class, Names.named("CanvasView"), CanvasView.class)
				.implement(SubView.class, Names.named("PropertiesView"), PropertiesView.class)
				.implement(SubView.class, Names.named("MenuView"), MenuView.class).build(SubViewFactory.class));
	}

	@Provides @Singleton
	NamespaceMap namespaceMap() {
		return namespaceMap;
	}

	@MainWindowRoot
	@Provides @Singleton
	Parent mainWindowRoot() {
		return mainWindowRoot;
	}

	@MainWindowStage
	@Provides @Singleton
	Stage mainWindowStage() {
		return mainWindowStage;
	}
}
