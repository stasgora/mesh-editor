package dev.sgora.mesheditor.view.sub;

import com.google.inject.name.Named;
import javafx.scene.layout.Region;
import dev.sgora.mesheditor.view.ViewType;

public interface SubViewFactory {
	@Named("CanvasView") SubView buildCanvasView(Region root, ViewType viewType);
	@Named("MenuView") SubView buildMenuView(Region root, ViewType viewType);
	@Named("PropertiesView") SubView buildPropertiesView(Region root, ViewType viewType);
}
