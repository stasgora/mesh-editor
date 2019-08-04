package stasgora.mesh.editor.model.project;

import io.github.stasgora.observetree.Observable;
import io.github.stasgora.observetree.SettableProperty;
import stasgora.mesh.editor.model.observables.BindableProperty;
import stasgora.mesh.editor.model.paint.SerializableColor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

public class VisualProperties extends PropertyContainer {
	public BindableProperty<Boolean> meshVisible = new BindableProperty<>();
	public BindableProperty<Double> meshTransparency = new BindableProperty<>();

	public BindableProperty<Boolean> imageVisible = new BindableProperty<>();
	public BindableProperty<Double> imageTransparency = new BindableProperty<>();

	public SettableProperty<MeshLayer> triangulationLayer = new SettableProperty<>();
	public SettableProperty<MeshLayer> voronoiDiagramLayer = new SettableProperty<>();
}
