package sgora.mesh.editor.model.project;

import javafx.scene.image.Image;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.observables.SettableProperty;

import java.io.*;

public class ProjectState extends ComplexObservable implements Serializable {

	private static final long serialVersionUID = 1L;

	public SettableProperty<Boolean> stateSaved = new SettableProperty<>(true);
	public SettableProperty<File> file = new SettableProperty<>();
	public SettableProperty<Boolean> loaded = new SettableProperty<>(false);

	public SettableObservable<Mesh> mesh = new SettableObservable<>();

	public SettableProperty<Image> baseImage = new SettableProperty<>();
	public final Rectangle imageBox = new Rectangle();
	public byte[] rawImageFile;

	public ProjectState() {
		// only notify on set
		addSubObservable(mesh);
		addSubObservable(baseImage);
	}

}
