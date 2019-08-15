package stasgora.mesh.editor.view.sub;

import com.google.inject.name.Named;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.view.ViewType;

public interface SubViewFactory {
	@Named("CanvasView") SubView buildCanvasView(Region root, ViewType viewType);
	@Named("MenuView") SubView buildMenuView(Region root, ViewType viewType);
	@Named("PropertiesView") SubView buildPropertiesView(Region root, ViewType viewType);
}
