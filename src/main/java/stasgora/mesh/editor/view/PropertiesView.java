package stasgora.mesh.editor.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import io.github.stasgora.observetree.SettableProperty;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.model.NamespaceMap;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.files.ConfigModelMapper;
import stasgora.mesh.editor.services.ui.PropertyTreeCellFactory;
import stasgora.mesh.editor.view.sub.SubView;

public class PropertiesView extends SubView {

	private VisualProperties visualProperties;
	private final SettableProperty<Boolean> stateSaved;
	private final ConfigModelMapper configModelMapper;
	private final PropertyTreeCellFactory propertyTreeCellFactory;

	public TreeView<String> propertyTree;

	@Inject
	PropertiesView(@Assisted Region root, @Assisted ViewType viewType, NamespaceMap viewNamespaces, VisualProperties visualProperties,
	               LoadState loadState, ConfigModelMapper configModelMapper, PropertyTreeCellFactory propertyTreeCellFactory) {
		super(root, viewType, viewNamespaces);
		this.visualProperties = visualProperties;
		this.stateSaved = loadState.stateSaved;
		this.configModelMapper = configModelMapper;
		this.propertyTreeCellFactory = propertyTreeCellFactory;

		init();
	}

	@Override
	protected void init() {
		visualProperties.addListener(() -> stateSaved.setAndNotify(false));
		configModelMapper.map(visualProperties, "default.visualProperties");
		propertyTree.setCellFactory(propertyTreeCellFactory);
	}
}
