package sgora.mesh.editor.model.data;

public class Rectangle extends ObservableModel {

	private Point position, size;

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
		onValueChanged();
	}

	public Point getSize() {
		return size;
	}

	public void setSize(Point size) {
		this.size = size;
		onValueChanged();
	}

}
