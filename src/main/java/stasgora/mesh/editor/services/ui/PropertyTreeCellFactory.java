package stasgora.mesh.editor.services.ui;

import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import stasgora.mesh.editor.interfaces.config.LangConfigReader;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.ui.properties.PropertyItemType;
import stasgora.mesh.editor.ui.properties.SliderTreeItem;

public class PropertyTreeCellFactory implements Callback<TreeView<String>, TreeCell<String>> {

	private LangConfigReader appLang;
	private VisualProperties visualProperties;

	public PropertyTreeCellFactory(LangConfigReader appLang, VisualProperties visualProperties) {
		this.appLang = appLang;
		this.visualProperties = visualProperties;
	}

	@Override
	public TreeCell<String> call(TreeView<String> param) {
		return new CheckBoxTreeCell<>() {
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
					setText(null);
				} else {
					if (!(getTreeItem() instanceof CheckBoxTreeItem)) {
						setGraphic(null);
						return;
					}
					if(getTreeItem() instanceof SliderTreeItem) {
						prepareSliderItem((SliderTreeItem) getTreeItem());
					}
				}
			}

			private void prepareSliderItem(SliderTreeItem item) {
				PropertyItemType itemType = item.getItemType();

				CheckBox checkBox = (CheckBox) getGraphic();
				checkBox.setTooltip(new Tooltip(appLang.getText("fxml.properties.tooltips.visibility")));
				visualProperties.propertyTypeToVisibleProperty.get(itemType).bindWithFxObservable(checkBox.selectedProperty());
				checkBox.setText(getText());
				setText(null);

				HBox vBox = new HBox(checkBox);
				vBox.setSpacing(5);
				if(itemType.showSlider) {
					Slider slider = new Slider(itemType.minSliderVal, itemType.maxSliderVal, itemType.minSliderVal);
					slider.setTooltip(new Tooltip(appLang.getText(itemType.sliderTooltipKey)));
					visualProperties.propertyTypeToSliderValue.get(itemType).bindWithFxObservable(slider.valueProperty());
					vBox.getChildren().add(slider);
				}
				setGraphic(vBox);
			}
		};
	}
}
