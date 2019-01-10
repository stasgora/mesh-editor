package sgora.mesh.editor.model.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Mesh extends ObservableModel {

	private final List<Point> nodes = new ArrayList<>();
	private final Rectangle imageBoxModel;

	public Mesh(Rectangle imageBoxModel) {
		this.imageBoxModel = imageBoxModel;
	}

	public void addNode(Point node) {
		nodes.add(node.subtract(imageBoxModel.getPosition()).divide(imageBoxModel.getSize()));
		onValueChanged();
	}

	public void removeNode(int nodeIndex) {
		nodes.remove(nodeIndex);
		onValueChanged();
	}

	public List<Point> getNodes() {
		return Collections.unmodifiableList(nodes.stream().map(node -> new Point(node).multiply(imageBoxModel.getSize()).add(imageBoxModel.getPosition())).collect(Collectors.toList()));
	}

}
