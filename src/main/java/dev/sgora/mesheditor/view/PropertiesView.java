package dev.sgora.mesheditor.view;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import dev.sgora.mesheditor.model.project.LoadState;
import dev.sgora.mesheditor.model.project.VisualProperties;
import dev.sgora.mesheditor.services.files.ConfigModelMapper;
import dev.sgora.mesheditor.services.ui.PropertyTreeCellFactory;
import io.github.stasgora.observetree.SettableProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import dev.sgora.mesheditor.model.NamespaceMap;
import dev.sgora.mesheditor.view.sub.SubView;

public class PropertiesView extends SubView {
	private VisualProperties visualProperties;
	private final SettableProperty<Boolean> stateSaved;
	private final ConfigModelMapper configModelMapper;
	private final PropertyTreeCellFactory propertyTreeCellFactory;

	@FXML
	private TreeView<String> propertyTree;

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
