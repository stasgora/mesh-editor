package stasgora.mesh.editor.services.mesh.generation;

import stasgora.mesh.editor.model.geom.Point;

public interface TriangulationService {

	void createNewMesh();

	boolean addNode(Point location);

	boolean removeNode(Point location);

	Point moveNode(Point node, Point position);

	Point findNodeByLocation(Point location);

}
