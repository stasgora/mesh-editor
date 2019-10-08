package dev.sgora.mesheditor.services.mesh.generation;

import dev.sgora.mesheditor.model.geom.Point;

public interface TriangulationService {

	void createNewMesh();

	boolean addNode(Point location);

	boolean removeNode(Point location);

	Point moveNode(Point node, Point position);

	Point findNodeByLocation(Point location);

}
