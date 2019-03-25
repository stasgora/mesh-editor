package sgora.mesh.editor.model.project;

import javafx.scene.image.Image;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.geom.Rectangle;
import sgora.mesh.editor.model.observables.Observable;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.observables.SettableProperty;

public class CanvasData extends Observable {

	public SettableObservable<Mesh> mesh = new SettableObservable<>();
	public final Rectangle imageBox = new Rectangle();

	public SettableProperty<Image> baseImage = new SettableProperty<>();
	public byte[] rawImageFile;

	public CanvasData() {
		addSubObservable(mesh);
		addSubObservable(baseImage);
		addSubObservable(imageBox);
	}

}
