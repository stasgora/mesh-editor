package dev.sgora.mesheditor.model.project;

import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.observables.BindableProperty;
import io.github.stasgora.observetree.SettableObservable;

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
