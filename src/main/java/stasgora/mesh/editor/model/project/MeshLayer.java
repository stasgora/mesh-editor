package stasgora.mesh.editor.model.project;

import stasgora.mesh.editor.model.observables.BindableProperty;

public class MeshLayer extends PropertyContainer {
	public BindableProperty<Boolean> layerVisible = new BindableProperty<>();
	public BindableProperty<Double> layerTransparency = new BindableProperty<>();

	public BindableProperty<Boolean> polygonsVisible = new BindableProperty<>();

	public BindableProperty<Boolean> nodesVisible = new BindableProperty<>();
	public BindableProperty<Double> nodeRadius = new BindableProperty<>();

	public BindableProperty<Boolean> edgesVisible = new BindableProperty<>();
	public BindableProperty<Double> edgeThickness = new BindableProperty<>();

	MeshLayer() {
		scan();
	}
}
