package sgora.mesh.editor.model.project;

import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.paint.SerializableColor;

import java.io.Serializable;

public class VisualProperties extends ComplexObservable implements Serializable {

	private static final long serialVersionUID = 6L;

	public SettableProperty<SerializableColor> nodeColor = new SettableProperty<>(new SerializableColor(0.1, 0.2, 1, 1));
	public SettableProperty<Integer> nodeRadius = new SettableProperty<>(8);

	public VisualProperties() {
		addSubObservable(nodeColor);
		addSubObservable(nodeRadius);
	}

}
