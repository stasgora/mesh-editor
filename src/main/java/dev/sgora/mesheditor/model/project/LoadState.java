package dev.sgora.mesheditor.model.project;

import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.observables.SettableList;
import io.github.stasgora.observetree.Observable;
import io.github.stasgora.observetree.SettableProperty;

import java.io.File;

@Singleton
public class LoadState extends Observable {

	public final SettableProperty<Boolean> stateSaved = new SettableProperty<>(true);
	public final SettableProperty<File> file = new SettableProperty<>();
	public final SettableProperty<Boolean> loaded = new SettableProperty<>(false);
	public final SettableList<File> recentProjects = new SettableList<>();

	LoadState() {
		addSubObservable(stateSaved);
		addSubObservable(file);
		addSubObservable(loaded);
		addSubObservable(recentProjects);
	}

}
