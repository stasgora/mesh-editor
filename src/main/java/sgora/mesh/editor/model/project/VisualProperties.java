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

	public SettableProperty<SerializableColor> nodeColor = new SettableProperty<>();
	public SettableProperty<Integer> nodeRadius = new SettableProperty<>();
	public SettableProperty<Integer> lineWidth = new SettableProperty<>();

	public BindableProperty<Boolean> meshVisible = new BindableProperty<>();
	public BindableProperty<Boolean> imageVisible = new BindableProperty<>();
	public BindableProperty<Boolean> nodesVisible = new BindableProperty<>();
	public BindableProperty<Boolean> edgesVisible = new BindableProperty<>();
	public BindableProperty<Boolean> trianglesVisible = new BindableProperty<>();
	public BindableProperty<Double> meshTransparency = new BindableProperty<>();

	private List<SettableProperty> properties = Arrays.asList(nodeColor, lineWidth, nodeRadius,
			meshVisible, imageVisible, nodesVisible, edgesVisible, trianglesVisible, meshTransparency);

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
