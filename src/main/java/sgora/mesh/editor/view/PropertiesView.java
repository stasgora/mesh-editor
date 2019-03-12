package sgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import sgora.mesh.editor.interfaces.SubController;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.VisualProperties;

public class PropertiesView implements SubController {

	public CheckBox meshVisibleCheckBox;
	public CheckBox imageVisibleCheckBox;

	@FXML
	private ObservableMap<String, Object> namespace;

	private SettableObservable<VisualProperties> visualProperties;

	public PropertiesView(SettableObservable<VisualProperties> visualProperties) {
		this.visualProperties = visualProperties;
	}

	public void init() {
		meshVisibleCheckBox.selectedProperty().addListener((observable, oldVal, newVal) -> visualProperties.get().meshVisible.set(newVal));
		imageVisibleCheckBox.selectedProperty().addListener(((observable, oldVal, newVal) -> visualProperties.get().imageVisible.set(newVal)));
	}

}
