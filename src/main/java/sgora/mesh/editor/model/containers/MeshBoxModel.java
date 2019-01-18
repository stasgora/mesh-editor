package sgora.mesh.editor.model.containers;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Mesh;
import sgora.mesh.editor.model.observables.ComplexObservable;
import sgora.mesh.editor.model.observables.ObservableProperty;

public class MeshBoxModel extends ComplexObservable {

	public final Mesh mesh = new Mesh();
	public ObservableProperty<Color> nodeColor = new ObservableProperty<>(new Color(0.1, 0.2, 1, 1));
	public ObservableProperty<Integer> nodeRadius = new ObservableProperty<>(8);

	MeshBoxModel() {
		notifyManually = false;
		addSubObservable(mesh);
		addSubObservable(nodeColor);
		addSubObservable(nodeRadius);
	}

	public MouseButton placeNodeButton = MouseButton.PRIMARY;
	public MouseButton removeNodeButton = MouseButton.SECONDARY;
	public MouseButton moveNodeButton = MouseButton.PRIMARY;

}
