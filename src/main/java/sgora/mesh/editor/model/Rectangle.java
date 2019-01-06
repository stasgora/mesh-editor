package sgora.mesh.editor.model;

public class Rectangle extends ObservableModel {

	private Point position, size;

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
		onValueSet();
	}

	public Point getSize() {
		return size;
	}

	public void setSize(Point size) {
		this.size = size;
		onValueSet();
	}

}
