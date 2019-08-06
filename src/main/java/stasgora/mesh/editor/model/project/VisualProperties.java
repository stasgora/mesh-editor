package stasgora.mesh.editor.model.project;

import io.github.stasgora.observetree.SettableObservable;
import stasgora.mesh.editor.model.observables.BindableProperty;

public class VisualProperties extends PropertyContainer {
	public BindableProperty<Boolean> meshVisible = new BindableProperty<>();
	public BindableProperty<Double> meshTransparency = new BindableProperty<>();

	public BindableProperty<Boolean> imageVisible = new BindableProperty<>();
	public BindableProperty<Double> imageTransparency = new BindableProperty<>();

	public SettableObservable<MeshLayer> triangulationLayer = new SettableObservable<>(new MeshLayer());
	public SettableObservable<MeshLayer> voronoiDiagramLayer = new SettableObservable<>(new MeshLayer());

	public VisualProperties() {
		scan();
	}
}
