package sgora.mesh.editor.view;

import javafx.scene.control.CheckBox;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.VisualProperties;

public class PropertiesView {

	public CheckBox meshVisibleCheckBox;
	public CheckBox imageVisibleCheckBox;

	private SettableObservable<VisualProperties> visualProperties;

	public void init(SettableObservable<VisualProperties> visualProperties) {
		this.visualProperties = visualProperties;
		meshVisibleCheckBox.selectedProperty().addListener((observable, oldVal, newVal) -> visualProperties.get().meshVisible.set(newVal));
		imageVisibleCheckBox.selectedProperty().addListener(((observable, oldVal, newVal) -> visualProperties.get().imageVisible.set(newVal)));
	}

}
