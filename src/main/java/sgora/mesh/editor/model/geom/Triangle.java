package sgora.mesh.editor.model.geom;

import sgora.mesh.editor.model.observables.ControlledObservable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Triangle extends ControlledObservable implements Serializable {

	public Point[] nodes;
	public Triangle[] triangles;

	public Triangle(Point[] nodes, Triangle[] triangles) {
		this.nodes = nodes;
		this.triangles = triangles;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(nodes);
		out.writeObject(triangles);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		nodes = (Point[]) in.readObject();
		triangles = (Triangle[]) in.readObject();
	}

}
