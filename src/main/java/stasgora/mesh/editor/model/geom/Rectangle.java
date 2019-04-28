package stasgora.mesh.editor.model.geom;

import io.github.stasgora.observetree.Observable;

import java.io.Serializable;

public class Rectangle extends Observable implements Serializable {

	public Point position, size;

	private static final long serialVersionUID = 1L;

	public Rectangle() {
		position = new Point();
		addSubObservable(position);
		size = new Point();
		addSubObservable(size);
	}

	public boolean contains(Point point) {
		return point.x >= position.x && point.x <= position.x + size.x && point.y >= position.y && point.y <= position.y + size.y;
	}

}
