package stasgora.mesh.editor.view;

import io.github.stasgora.observetree.SettableProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.enums.ViewType;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.mapping.ConfigModelMapper;
import stasgora.mesh.editor.services.ui.PropertyTreeCellFactory;

import java.util.Map;

public class PropertiesView extends SubController {

	public TextField meshTransparencyValue;
	public Slider meshTransparencySlider;

	private VisualProperties visualProperties;
	private final ConfigModelMapper configModelMapper;

	public TreeView<String> propertyTree;

	private static final int MIN_SLIDER_VAL = 0, MAX_SLIDER_VAL = 100;

	public PropertiesView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces, VisualProperties visualProperties,
	                      SettableProperty<Boolean> stateSaved, ConfigModelMapper configModelMapper, PropertyTreeCellFactory propertyTreeCellFactory) {
		super(root, viewType, viewNamespaces);
		this.visualProperties = visualProperties;
		this.configModelMapper = configModelMapper;

		visualProperties.addListener(() -> stateSaved.setAndNotify(false));
		propertyTree.setCellFactory(propertyTreeCellFactory);

		init();
	}

	public void init() {
		visualProperties.meshTransparency.bindWithFxObservable(meshTransparencyValue.textProperty(), val -> String.valueOf((int) (val * 100)), val -> textToRange(val) / 100d);
		meshTransparencySlider.valueProperty().addListener((observable, oldVal, newVal) -> meshTransparencyValue.setText(String.valueOf(newVal.intValue())));
		meshTransparencyValue.textProperty().addListener((observable, oldVal, newVal) -> setMeshTransparency(textToRange(newVal)));

		//configModelMapper.mapConfigPathToModelObject(visualProperties, "default.visualProperties", true);
	}

	private void setMeshTransparency(int value) {
		meshTransparencyValue.setText(String.valueOf(value));
		meshTransparencySlider.setValue(value);
	}

	private int textToRange(String value) {
		value = value.replace("\\.[0-9]*", "");
		if (value.isEmpty()) {
			return 0;
		}
		return Math.max(MIN_SLIDER_VAL, Math.min(MAX_SLIDER_VAL, Integer.valueOf(value)));
	}

}
