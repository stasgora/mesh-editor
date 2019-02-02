package sgora.mesh.editor.model.containers;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.SettableProperty;

public class MeshBoxModel extends ComplexObservable {

	public SettableProperty<Color> nodeColor = new SettableProperty<>(new Color(0.1, 0.2, 1, 1));
	public SettableProperty<Integer> nodeRadius = new SettableProperty<>(8);

	MeshBoxModel() {
		notifyManually = false;
		addSubObservable(nodeColor);
		addSubObservable(nodeRadius);
	}

	public MouseButton placeNodeButton = MouseButton.PRIMARY;
	public MouseButton removeNodeButton = MouseButton.SECONDARY;
	public MouseButton moveNodeButton = MouseButton.PRIMARY;

}
