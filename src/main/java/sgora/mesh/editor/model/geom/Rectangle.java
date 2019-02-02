package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.observables.ComplexObservable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Rectangle extends ComplexObservable implements Serializable {

	public Point position, size;

	public Rectangle() {
		position = new Point();
		addSubObservable(position);
		size = new Point();
		addSubObservable(size);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(position);
		out.writeObject(size);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		position = (Point) in.readObject();
		size = (Point) in.readObject();
	}

}
