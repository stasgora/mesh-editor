package stasgora.mesh.editor.model.project;

import com.google.inject.Singleton;
import io.github.stasgora.observetree.SettableObservable;
import stasgora.mesh.editor.model.observables.BindableProperty;

@Singleton
public class VisualProperties extends PropertyContainer {
	public final BindableProperty<Boolean> meshVisible = new BindableProperty<>();
	public final BindableProperty<Double> meshTransparency = new BindableProperty<>();

	public final BindableProperty<Boolean> imageVisible = new BindableProperty<>();
	public final BindableProperty<Double> imageTransparency = new BindableProperty<>();

	public final SettableObservable<MeshLayer> triangulationLayer = new SettableObservable<>(new MeshLayer());
	public final SettableObservable<MeshLayer> voronoiDiagramLayer = new SettableObservable<>(new MeshLayer());

	VisualProperties() {
		scan();
	}
}
