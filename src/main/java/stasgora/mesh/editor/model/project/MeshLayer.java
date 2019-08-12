package stasgora.mesh.editor.model.project;

import stasgora.mesh.editor.model.observables.BindableProperty;

public class MeshLayer extends PropertyContainer {
	public final BindableProperty<Boolean> layerVisible = new BindableProperty<>();
	public final BindableProperty<Double> layerTransparency = new BindableProperty<>();

	public final BindableProperty<Boolean> polygonsVisible = new BindableProperty<>();

	public final BindableProperty<Boolean> nodesVisible = new BindableProperty<>();
	public final BindableProperty<Double> nodeRadius = new BindableProperty<>();

	public final BindableProperty<Boolean> edgesVisible = new BindableProperty<>();
	public final BindableProperty<Double> edgeThickness = new BindableProperty<>();

	MeshLayer() {
		scan();
	}
}
