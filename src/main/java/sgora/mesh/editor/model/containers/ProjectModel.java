package sgora.mesh.editor.model.containers;

import sgora.mesh.editor.model.observables.ObservableProperty;

import java.io.File;

public class ProjectModel {

	public ObservableProperty<String> name = new ObservableProperty<>("Untitled");
	public ObservableProperty<Boolean> loaded = new ObservableProperty<>(false);
	public ObservableProperty<Boolean> stateSaved = new ObservableProperty<>(true);
	public File file;

}
