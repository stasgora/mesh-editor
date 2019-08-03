package stasgora.mesh.editor.services.mesh.voronoi;

import io.github.stasgora.observetree.SettableObservable;
import stasgora.mesh.editor.model.geom.Line;
import stasgora.mesh.editor.model.geom.Mesh;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.model.geom.PointPolygon;
import stasgora.mesh.editor.model.geom.polygons.Triangle;
import stasgora.mesh.editor.model.project.MeshType;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.mesh.triangulation.NodeUtils;

import java.util.*;
import java.util.logging.Logger;

public class VoronoiDiagramService {
	private final Logger LOGGER = Logger.getLogger(getClass().getName());

	private SettableObservable<Mesh> mesh;
	private NodeUtils nodeUtils;

	public VoronoiDiagramService(SettableObservable<Mesh> mesh, NodeUtils nodeUtils, VisualProperties visualProperties) {
		this.mesh = mesh;
		this.nodeUtils = nodeUtils;
		visualProperties.meshType.addListener(() -> {
			if(visualProperties.meshType.get() == MeshType.VORONOI_DIAGRAM)
				generateDiagram(mesh.get().getNodes());
		});
	}

	public void generateDiagram(List<Point> nodes) {
		Map<Triangle, Point> triangleCircumcenterMap = new HashMap<>();
		for (Point node : nodes) {
			PointPolygon pointPolygon = mesh.get().getPointPolygon(node);
			if(pointPolygon == null) {
				LOGGER.warning("Mesh does not contain given node");
				continue;
			}
			List<Triangle> triangles = new ArrayList<>();
			nodeUtils.getNodeNeighbours(node, nodeUtils.findNodeTriangle(node), null, triangles);
			List<Point> vertices = new ArrayList<>();
			for (Triangle triangle : triangles) {
				if(!triangleCircumcenterMap.containsKey(triangle)) {
					vertices.add(triangle.circumcenter());
					triangleCircumcenterMap.put(triangle, vertices.get(vertices.size() - 1));
				} else {
					vertices.add(triangleCircumcenterMap.get(triangle));
				}
			}
			if(vertices.size() >= 3) //temp
				pointPolygon.polygon.nodes = vertices.toArray(Point[]::new);
		}
	}

}
