package sgora.mesh.editor.model.project;

import sgora.mesh.editor.model.observables.Observable;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.paint.SerializableColor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class VisualProperties extends Observable implements Serializable {

	private static final long serialVersionUID = 1L;

	public SettableProperty<SerializableColor> nodeColor = new SettableProperty<>(new SerializableColor(0.1, 0.2, 1, 1));
	public SettableProperty<Integer> nodeRadius = new SettableProperty<>(8);

	public SettableProperty<Boolean> meshVisible = new SettableProperty<>(true);
	public SettableProperty<Boolean> imageVisible = new SettableProperty<>(true);
	public SettableProperty<Double> meshTransparency = new SettableProperty<>();

	public VisualProperties() {
		addSubObservables();
	}

	private void addSubObservables() {
		addSubObservable(nodeColor);
		addSubObservable(nodeRadius);

		addSubObservable(meshVisible);
		addSubObservable(imageVisible);
		addSubObservable(meshTransparency);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		addSubObservables();
	}

}
