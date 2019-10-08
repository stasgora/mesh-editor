package dev.sgora.mesheditor.services.mesh.generation;

import com.google.inject.Inject;
import dev.sgora.mesheditor.model.geom.Mesh;
import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.model.project.CanvasData;
import io.github.stasgora.observetree.SettableObservable;
import dev.sgora.mesheditor.model.geom.polygons.Polygon;
import dev.sgora.mesheditor.model.geom.polygons.Triangle;

import java.util.*;
import java.util.logging.Logger;

public class VoronoiDiagramService {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final SettableObservable<Mesh> mesh;
	private final NodeUtils nodeUtils;

	@Inject
	VoronoiDiagramService(CanvasData canvasData, NodeUtils nodeUtils) {
		this.mesh = canvasData.mesh;
		this.nodeUtils = nodeUtils;
	}

	public void generateDiagram(Collection<Point> nodes) {
		Map<Triangle, Point> triangleCircumcenterMap = new HashMap<>();
		for (Point node : nodes) {
			Polygon pointRegion = mesh.get().getPointRegion(node);
			if (pointRegion == null) {
				if (!mesh.get().getBoundingNodes().contains(node))
					logger.warning("Mesh does not contain given node");
				continue;
			}
			List<Triangle> triangles = new ArrayList<>();
			nodeUtils.getNodeNeighbours(node, nodeUtils.findNodeTriangle(node), null, triangles);
			List<Point> vertices = new ArrayList<>();
			for (Triangle triangle : triangles) {
				if (!triangleCircumcenterMap.containsKey(triangle)) {
					vertices.add(triangle.circumcenter());
					triangleCircumcenterMap.put(triangle, vertices.get(vertices.size() - 1));
				} else {
					vertices.add(triangleCircumcenterMap.get(triangle));
				}
			}
			pointRegion.setNodes(vertices.toArray(Point[]::new));
		}
	}

}
