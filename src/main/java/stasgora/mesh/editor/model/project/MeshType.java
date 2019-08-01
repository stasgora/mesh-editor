package stasgora.mesh.editor.model.project;

public enum MeshType {
	TRIANGULATION("triangulation"),
	VORONOI_DIAGRAM("voronoiDiagram");

	private static final String KEY_PREFIX = "fxml.properties.tree.mesh.type.";

	private String textKey;

	MeshType(String textKey) {
		this.textKey = textKey;
	}

	public String getTextKey() {
		return KEY_PREFIX + textKey;
	}
}
