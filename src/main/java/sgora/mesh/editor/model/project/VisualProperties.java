package sgora.mesh.editor.model.project;

import sgora.mesh.editor.model.observables.BindableProperty;
import sgora.mesh.editor.model.observables.Observable;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.paint.SerializableColor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VisualProperties extends Observable {

	public SettableProperty<SerializableColor> nodeColor = new SettableProperty<>(new SerializableColor(0.1, 0.2, 1, 1));
	public SettableProperty<Integer> nodeRadius = new SettableProperty<>(8);

	public BindableProperty<Boolean> meshVisible = new BindableProperty<>(true);
	public BindableProperty<Boolean> imageVisible = new BindableProperty<>(true);
	public BindableProperty<Double> meshTransparency = new BindableProperty<>();

	private List<SettableProperty> properties = Arrays.asList(nodeColor, nodeRadius, meshVisible, imageVisible, meshTransparency);

	public VisualProperties() {
		properties.forEach(this::addSubObservable);
	}

	public void writeProperties(ObjectOutputStream out) throws IOException {
		for (SettableProperty obj : properties) {
			out.writeObject(obj.get());
		}
	}

	public void readProperties(ObjectInputStream in) throws IOException, ClassNotFoundException {
		for (SettableProperty obj : properties) {
			obj.set(in.readObject());
		}
		notifyListeners();
	}

}
