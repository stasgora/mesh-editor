package stasgora.mesh.editor.services.mesh.generation;

import com.google.inject.Inject;
import io.github.stasgora.observetree.SettableObservable;
import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.polygons.Polygon;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.project.CanvasData;
import stasgora.mesh.editor.services.mesh.generation.NodeUtils;

import java.util.*;
import java.util.logging.Logger;

public class VoronoiDiagramService {
	private final Logger LOGGER = Logger.getLogger(getClass().getName());

	private SettableObservable<Mesh> mesh;
	private NodeUtils nodeUtils;

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
				if (!mesh.get().boundingNodes.contains(node))
					LOGGER.warning("Mesh does not contain given node");
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
			pointRegion.nodes = vertices.toArray(Point[]::new);
		}
	}

}
