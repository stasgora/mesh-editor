package stasgora.mesh.editor.ui.properties;

import javafx.scene.control.CheckBoxTreeItem;

public class SliderTreeItem<T> extends CheckBoxTreeItem<T> {
	private PropertyItemType itemType;

	public PropertyItemType getItemType() {
		return itemType;
	}

	public void setItemType(PropertyItemType itemType) {
		this.itemType = itemType;
	}

}
