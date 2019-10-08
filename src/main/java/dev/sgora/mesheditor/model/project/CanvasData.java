package dev.sgora.mesheditor.model.project;

import com.google.inject.Singleton;
import io.github.stasgora.observetree.Observable;
import io.github.stasgora.observetree.SettableObservable;
import io.github.stasgora.observetree.SettableProperty;
import javafx.scene.image.Image;
import dev.sgora.mesheditor.model.geom.Mesh;
import dev.sgora.mesheditor.model.geom.polygons.Rectangle;

@Singleton
public class CanvasData extends Observable {
	public final SettableObservable<Mesh> mesh = new SettableObservable<>();
	public final Rectangle imageBox = new Rectangle();

	public final SettableProperty<Image> baseImage = new SettableProperty<>();
	private byte[] rawImageFile;

	CanvasData() {
		addSubObservable(mesh);
		addSubObservable(baseImage);
		addSubObservable(imageBox);
	}

	public byte[] getRawImageFile() {
		return rawImageFile;
	}

	public void setRawImageFile(byte[] rawImageFile) {
		this.rawImageFile = rawImageFile;
	}
}
