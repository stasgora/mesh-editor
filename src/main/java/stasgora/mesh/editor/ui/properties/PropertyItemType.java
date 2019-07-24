package stasgora.mesh.editor.ui.properties;

public enum PropertyItemType {

	IMAGE("transparency"),
	MESH("transparency"),
	EDGES("edgeThickness", "meshBox.edgeThickness"),
	NODES("nodeRadius", "meshBox.nodeRadius"),
	TRIANGLES;

	private static final String SLIDER_KEY_PREFIX = "fxml.properties.tooltips.";

	private String tooltipKey, valueKey;
	public boolean showSlider = true;

	PropertyItemType(String tooltipKey) {
		this.tooltipKey = tooltipKey;
	}

	PropertyItemType(String tooltipKey, String valueKey) {
		this.tooltipKey = tooltipKey;
		this.valueKey = valueKey;
	}

	PropertyItemType() {
		this.showSlider = false;
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
