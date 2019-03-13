package sgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.ViewType;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.VisualProperties;

import java.util.Map;

public class PropertiesView extends SubController {

	public CheckBox meshVisibleCheckBox;
	public CheckBox imageVisibleCheckBox;

	private SettableObservable<VisualProperties> visualProperties;

	public PropertiesView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces, SettableObservable<VisualProperties> visualProperties) {
		super(root, viewType, viewNamespaces);
		this.visualProperties = visualProperties;
		init();
	}

	public void init() {
		meshVisibleCheckBox.selectedProperty().addListener((observable, oldVal, newVal) -> visualProperties.get().meshVisible.set(newVal));
		imageVisibleCheckBox.selectedProperty().addListener(((observable, oldVal, newVal) -> visualProperties.get().imageVisible.set(newVal)));
	}

}
