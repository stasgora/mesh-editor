package stasgora.mesh.editor.ui.properties;

public enum PropertyItemType {

	IMAGE("image", "transparency"),
	MESH("mesh.title", "transparency"),
	TRIANGULATION("mesh.layer.triangulation", "transparency"),
	VORONOI_DIAGRAM("mesh.layer.voronoiDiagram", "transparency"),

	POLYGONS("mesh.polygons"),
	EDGES("mesh.edges", "edgeThickness", "meshBox.edgeThickness"),
	NODES("mesh.nodes", "nodeRadius", "meshBox.nodeRadius");

	private static final String KEY_PREFIX = "fxml.properties.";
	private static final String TEXT_KEY_PREFIX = KEY_PREFIX + "tree.";
	private static final String SLIDER_KEY_PREFIX = KEY_PREFIX + "tooltips.";

	private String textKey, tooltipKey, valueKey;

	PropertyItemType(String textKey, String tooltipKey) {
		this.textKey = textKey;
		this.tooltipKey = tooltipKey;
	}

	PropertyItemType(String textKey, String tooltipKey, String valueKey) {
		this.textKey = textKey;
		this.tooltipKey = tooltipKey;
		this.valueKey = valueKey;
	}

	PropertyItemType(String textKey) {
		this.textKey = textKey;
	}

	public String getTextKey() {
		return TEXT_KEY_PREFIX + textKey;
	}

	public String getSliderKey() {
		return SLIDER_KEY_PREFIX + tooltipKey;
	}

	public String getMinValueKey() {
		if(valueKey == null) return null;
		return valueKey + ".min";
	}

	public String getMaxValueKey() {
		if(valueKey == null) return null;
		return valueKey + ".max";
	}
}
