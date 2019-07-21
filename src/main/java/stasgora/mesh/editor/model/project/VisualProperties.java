package stasgora.mesh.editor.model.project;

import io.github.stasgora.observetree.Observable;
import io.github.stasgora.observetree.SettableProperty;
import stasgora.mesh.editor.model.observables.BindableProperty;
import stasgora.mesh.editor.model.paint.SerializableColor;
import stasgora.mesh.editor.ui.properties.PropertyItemType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VisualProperties extends Observable {

	public SettableProperty<SerializableColor> nodeColor = new SettableProperty<>();
	public BindableProperty<Double> nodeRadius = new BindableProperty<>();
	public BindableProperty<Double> lineWidth = new BindableProperty<>();

	public BindableProperty<Boolean> meshVisible = new BindableProperty<>();
	public BindableProperty<Boolean> imageVisible = new BindableProperty<>();
	public BindableProperty<Boolean> nodesVisible = new BindableProperty<>();
	public BindableProperty<Boolean> edgesVisible = new BindableProperty<>();
	public BindableProperty<Boolean> trianglesVisible = new BindableProperty<>();

	public BindableProperty<Double> meshTransparency = new BindableProperty<>();
	public BindableProperty<Double> imageTransparency = new BindableProperty<>();

	public Map<PropertyItemType, BindableProperty> propertyTypeToVisibleProperty = Map.of(
			PropertyItemType.IMAGE, imageVisible,
			PropertyItemType.MESH, meshVisible,
			PropertyItemType.TRIANGLES, trianglesVisible,
			PropertyItemType.NODES, nodesVisible,
			PropertyItemType.EDGES, edgesVisible
	);

	public Map<PropertyItemType, BindableProperty> propertyTypeToSliderValue = Map.of(
			PropertyItemType.IMAGE, imageTransparency,
			PropertyItemType.MESH, meshTransparency,
			PropertyItemType.NODES, nodeRadius,
			PropertyItemType.EDGES, lineWidth
	);

	private List<SettableProperty> properties = Arrays.asList(nodeColor, lineWidth, nodeRadius,
			meshVisible, imageVisible, nodesVisible, edgesVisible, trianglesVisible, meshTransparency, imageTransparency);

	public VisualProperties() {
		properties.forEach(this::addSubObservable);
	}

	public void writeProperties(ObjectOutputStream out) throws IOException {
		for (SettableProperty obj : properties) {
			out.writeObject(obj.get());
		}
	}

	public void readProperties(ObjectInputStream in) throws IOException, ClassNotFoundException {
		for (SettableProperty obj : properties) {
			obj.set(in.readObject());
		}
		notifyListeners();
	}

	public void saveDefaultValues() {
		properties.forEach(SettableProperty::saveAsDefaultValue);
	}

	public void restoreDefaultValues() {
		properties.forEach(SettableProperty::resetToDefaultValue);
	}

}
