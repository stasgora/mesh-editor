package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.observables.ComplexObservable;

public class Rectangle extends ComplexObservable {

	public final Point position, size;

	public Rectangle() {
		position = new Point();
		addSubObservable(position);
		size = new Point();
		addSubObservable(size);
	}

}
