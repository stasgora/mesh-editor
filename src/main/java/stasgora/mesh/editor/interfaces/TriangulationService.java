package stasgora.mesh.editor.interfaces;

import stasgora.mesh.editor.model.geom.Point;

public interface TriangulationService {

	void createNewMesh();

	void addNode(Point location);

	Point findNodeByLocation(Point location);

	boolean removeNode(Point location);

	void moveNode(Point node, Point position);

}
