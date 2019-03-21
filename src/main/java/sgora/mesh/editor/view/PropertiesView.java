package sgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.ViewType;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.project.VisualProperties;

import java.util.Map;

public class PropertiesView extends SubController {

	public CheckBox meshVisibleCheckBox;
	public CheckBox imageVisibleCheckBox;

	public TextField meshTransparencyValue;
	public Slider meshTransparencySlider;

	private VisualProperties visualProperties;

	private static final int MIN_SLIDER_VAL = 0, MAX_SLIDER_VAL = 100;

	public PropertiesView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces,
	                      VisualProperties visualProperties, SettableProperty<Boolean> stateSaved) {
		super(root, viewType, viewNamespaces);
		this.visualProperties = visualProperties;

		visualProperties.addListener(() -> stateSaved.setAndNotify(false));
		init();
	}

	public void init() {
		visualProperties.meshVisible.bindWithFxObservable(meshVisibleCheckBox.selectedProperty());
		visualProperties.imageVisible.bindWithFxObservable(imageVisibleCheckBox.selectedProperty());

		visualProperties.meshTransparency.bindWithFxObservable(meshTransparencyValue.textProperty(), val -> String.valueOf((int) (val * 100)), val -> textToRange(val) / 100d);
		meshTransparencySlider.valueProperty().addListener((observable, oldVal, newVal) -> meshTransparencyValue.setText(String.valueOf(newVal.intValue())));
		meshTransparencyValue.textProperty().addListener((observable, oldVal, newVal) -> setMeshTransparency(textToRange(newVal)));
	}

	private void setMeshTransparency(int value) {
		meshTransparencyValue.setText(String.valueOf(value));
		meshTransparencySlider.setValue(value);
	}

	private int textToRange(String value) {
		value = value.replace("\\.[0-9]*", "");
		if(value.isEmpty()) {
			return 0;
		}
		return Math.max(MIN_SLIDER_VAL, Math.min(MAX_SLIDER_VAL, Integer.valueOf(value)));
	}

}
