package stasgora.mesh.editor.ui.properties;

import javafx.scene.control.TreeItem;

public class PropertyTreeItem<T> extends TreeItem<T> {
	private PropertyItemType itemType;
	private boolean hasCheckBox = true;
	private boolean hasSlider;
	private boolean hasComboBox;

	private double sliderChangeStartValue;

	public PropertyItemType getItemType() {
		return itemType;
	}

	public void setItemType(PropertyItemType itemType) {
		this.itemType = itemType;
	}

	public boolean isHasCheckBox() {
		return hasCheckBox;
	}

	public void setHasCheckBox(boolean hasCheckBox) {
		this.hasCheckBox = hasCheckBox;
	}

	public boolean isHasSlider() {
		return hasSlider;
	}

	public void setHasSlider(boolean hasSlider) {
		this.hasSlider = hasSlider;
	}

	public boolean isHasComboBox() {
		return hasComboBox;
	}

	public void setHasComboBox(boolean hasComboBox) {
		this.hasComboBox = hasComboBox;
	}

	public double getSliderChangeStartValue() {
		return sliderChangeStartValue;
	}

	public void setSliderChangeStartValue(double sliderChangeStartValue) {
		this.sliderChangeStartValue = sliderChangeStartValue;
	}
}
