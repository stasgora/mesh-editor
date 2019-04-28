package stasgora.mesh.editor.model.project;

import io.github.stasgora.observetree.Observable;
import io.github.stasgora.observetree.SettableProperty;

import java.io.File;

public class LoadState extends Observable {

	public SettableProperty<Boolean> stateSaved = new SettableProperty<>(true);
	public SettableProperty<File> file = new SettableProperty<>();
	public SettableProperty<Boolean> loaded = new SettableProperty<>(false);

	public LoadState() {
		addSubObservable(stateSaved);
		addSubObservable(file);
		addSubObservable(loaded);
	}

}
