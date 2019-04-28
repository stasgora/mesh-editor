package stasgora.mesh.editor.model.project;

import io.github.stasgora.observetree.Observable;
import javafx.scene.image.Image;
import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Rectangle;
import io.github.stasgora.observetree.SettableObservable;
import io.github.stasgora.observetree.SettableProperty;

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
