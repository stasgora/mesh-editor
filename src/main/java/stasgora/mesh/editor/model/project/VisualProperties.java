package stasgora.mesh.editor.model.project;

import stasgora.mesh.editor.model.observables.BindableProperty;

public class VisualProperties extends PropertyContainer {
	public BindableProperty<Boolean> meshVisible = new BindableProperty<>();
	public BindableProperty<Double> meshTransparency = new BindableProperty<>();

	public BindableProperty<Boolean> imageVisible = new BindableProperty<>();
	public BindableProperty<Double> imageTransparency = new BindableProperty<>();

	public MeshLayer triangulationLayer = new MeshLayer();
	public MeshLayer voronoiDiagramLayer = new MeshLayer();

	public VisualProperties() {
		scan();
	}
}
