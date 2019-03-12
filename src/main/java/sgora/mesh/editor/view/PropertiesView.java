package sgora.mesh.editor.view;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.SubView;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.VisualProperties;

public class PropertiesView extends SubController {

	public CheckBox meshVisibleCheckBox;
	public CheckBox imageVisibleCheckBox;

	private SettableObservable<VisualProperties> visualProperties;

	public PropertiesView(Region root, SubView subView, SettableObservable<VisualProperties> visualProperties) {
		super(root, subView);
		this.visualProperties = visualProperties;
		loadView();
	}

	public void init() {
		meshVisibleCheckBox.selectedProperty().addListener((observable, oldVal, newVal) -> visualProperties.get().meshVisible.set(newVal));
		imageVisibleCheckBox.selectedProperty().addListener(((observable, oldVal, newVal) -> visualProperties.get().imageVisible.set(newVal)));
	}

}
