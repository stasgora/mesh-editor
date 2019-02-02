package sgora.mesh.editor.model.containers;

import javafx.scene.image.Image;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.observables.SettableProperty;

import java.io.*;

public class ProjectModel extends ComplexObservable {

	public SettableProperty<String> name = new SettableProperty<>();
	public SettableProperty<Boolean> stateSaved = new SettableProperty<>(true);
	public SettableProperty<File> file = new SettableProperty<>();
	public SettableProperty<Boolean> loaded = new SettableProperty<>(false);

	public SettableObservable<Mesh> mesh = new SettableObservable<>();

	public SettableProperty<Image> baseImage = new SettableProperty<>();
	public byte[] rawImageFile;

	public ProjectModel() {
		// only notify on set
		addSubObservable(mesh);
		addSubObservable(baseImage);
	}

}
