package stasgora.mesh.editor.ui.properties;

public enum PropertyItemType {
	IMAGE("fxml.properties.tooltips.transparency"),
	MESH("fxml.properties.tooltips.transparency"),
	EDGES("fxml.properties.tooltips.edgeThickness"),
	NODES("fxml.properties.tooltips.nodeSize"),
	TRIANGLES;

	public String sliderTooltip;
	public boolean showSlider = true;

	PropertyItemType(String sliderTooltip) {
		this.sliderTooltip = sliderTooltip;
	}

	PropertyItemType() {
		this.showSlider = false;
	}
}
