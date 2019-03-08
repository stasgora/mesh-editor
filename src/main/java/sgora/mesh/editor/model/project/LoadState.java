package sgora.mesh.editor.model.project;

import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.SettableProperty;

import java.io.File;

public class LoadState extends ComplexObservable {

	public SettableProperty<Boolean> stateSaved = new SettableProperty<>(true);
	public SettableProperty<File> file = new SettableProperty<>();
	public SettableProperty<Boolean> loaded = new SettableProperty<>(false);

	public LoadState() {
		addSubObservable(stateSaved);
		addSubObservable(file);
		addSubObservable(loaded);
	}

}
