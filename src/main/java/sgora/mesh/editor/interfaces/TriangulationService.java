package sgora.mesh.editor.interfaces;

import sgora.mesh.editor.model.geom.Point;

public interface TriangulationService {

	void createNewMesh();

	void addNode(Point location);

	Point findNodeByLocation(Point location);

	void removeNode(Point location);

	void moveNode(Point node, Point position);

}
