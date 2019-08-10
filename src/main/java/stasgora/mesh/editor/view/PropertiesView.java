package stasgora.mesh.editor.view;

import io.github.stasgora.observetree.SettableProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.mapping.ConfigModelMapper;
import stasgora.mesh.editor.services.ui.PropertyTreeCellFactory;

import java.util.Map;

public class PropertiesView extends SubController {

	private VisualProperties visualProperties;
	private final SettableProperty<Boolean> stateSaved;
	private final ConfigModelMapper configModelMapper;
	private final PropertyTreeCellFactory propertyTreeCellFactory;

	public TreeView<String> propertyTree;

	public PropertiesView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces, VisualProperties visualProperties,
	                      SettableProperty<Boolean> stateSaved, ConfigModelMapper configModelMapper, PropertyTreeCellFactory propertyTreeCellFactory) {
		super(root, viewType, viewNamespaces);
		this.visualProperties = visualProperties;
		this.stateSaved = stateSaved;
		this.configModelMapper = configModelMapper;
		this.propertyTreeCellFactory = propertyTreeCellFactory;

		init();
	}

	@Override
	void init() {
		visualProperties.addListener(() -> stateSaved.setAndNotify(false));
		configModelMapper.map(visualProperties, "default.visualProperties");
		propertyTree.setCellFactory(propertyTreeCellFactory);
	}
}
