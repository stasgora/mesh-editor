package stasgora.mesh.editor.ui.properties;

public enum PropertyItemType {

	IMAGE("fxml.properties.tooltips.transparency"),
	MESH("fxml.properties.tooltips.transparency"),
	EDGES("fxml.properties.tooltips.edgeThickness", 1, 10),
	NODES("fxml.properties.tooltips.nodeSize", 2, 20),
	TRIANGLES;

	public String sliderTooltipKey;
	public boolean showSlider = true;
	public double minSliderVal = 0, maxSliderVal = 1;

	PropertyItemType(String sliderTooltipKey, double minSliderVal, double maxSliderVal) {
		this.sliderTooltipKey = sliderTooltipKey;
		this.minSliderVal = minSliderVal;
		this.maxSliderVal = maxSliderVal;
	}

	PropertyItemType(String sliderTooltipKey) {
		this.sliderTooltipKey = sliderTooltipKey;
	}

	PropertyItemType() {
		this.showSlider = false;
	}
}
