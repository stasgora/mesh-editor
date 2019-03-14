package sgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.ViewType;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.VisualProperties;

import java.util.Map;

public class PropertiesView extends SubController {

	public CheckBox meshVisibleCheckBox;
	public CheckBox imageVisibleCheckBox;

	public TextField meshTransparencyValue;
	public Slider meshTransparencySlider;

	private SettableObservable<VisualProperties> visualProperties;

	public PropertiesView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces, SettableObservable<VisualProperties> visualProperties) {
		super(root, viewType, viewNamespaces);
		this.visualProperties = visualProperties;
		init();
	}

	public void init() {
		meshVisibleCheckBox.selectedProperty().addListener((observable, oldVal, newVal) -> visualProperties.get().meshVisible.set(newVal));
		imageVisibleCheckBox.selectedProperty().addListener(((observable, oldVal, newVal) -> visualProperties.get().imageVisible.set(newVal)));

		meshTransparencySlider.valueProperty().addListener((observable, oldVal, newVal) -> meshTransparencyValue.setText(String.valueOf(newVal.intValue())));
		meshTransparencyValue.textProperty().addListener((observable, oldVal, newVal) -> setMeshTransparency(validateNumericalText(newVal, 0, 100)));
		meshTransparencyValue.setText("0");
	}

	private void setMeshTransparency(int value) {
		meshTransparencyValue.setText(String.valueOf(value));
		meshTransparencySlider.setValue(value);
	}

	private int validateNumericalText(String value, int minVal, int maxVal) {
		value = value.replaceAll("[^0-9]", "");
		if(value.isEmpty()) {
			return 0;
		}
		return Math.max(minVal, Math.min(maxVal, Integer.valueOf(value)));
	}

}
