package stasgora.mesh.editor.services.ui;

import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import stasgora.mesh.editor.interfaces.action.history.ActionHistoryService;
import stasgora.mesh.editor.interfaces.config.AppConfigReader;
import stasgora.mesh.editor.interfaces.config.LangConfigReader;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.history.actions.property.LayerSliderChangeAction;
import stasgora.mesh.editor.services.history.actions.property.LayerVisibilityChangeAction;
import stasgora.mesh.editor.ui.properties.PropertyItemType;
import stasgora.mesh.editor.ui.properties.SliderTreeItem;

public class PropertyTreeCellFactory implements Callback<TreeView<String>, TreeCell<String>> {

	private LangConfigReader appLang;
	private AppConfigReader appConfig;
	private VisualProperties visualProperties;
	private ActionHistoryService actionHistoryService;

	public PropertyTreeCellFactory(LangConfigReader appLang, AppConfigReader appConfig, VisualProperties visualProperties, ActionHistoryService actionHistoryService) {
		this.appLang = appLang;
		this.appConfig = appConfig;
		this.visualProperties = visualProperties;
		this.actionHistoryService = actionHistoryService;
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
					CheckBox itemCheckBox = (CheckBox) getGraphic();
					itemCheckBox.setOnAction(event -> actionHistoryService.registerAction(new LayerVisibilityChangeAction(itemCheckBox.isSelected(), itemCheckBox::setSelected)));
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
					double minValue = getSliderConfigValue(itemType.getMinValueKey(), 0);
					Slider slider = new Slider(minValue, getSliderConfigValue(itemType.getMaxValueKey(), 1), minValue);
					slider.setTooltip(new Tooltip(appLang.getText(itemType.getSliderKey())));
					visualProperties.propertyTypeToSliderValue.get(itemType).bindWithFxObservable(slider.valueProperty());

					slider.valueChangingProperty().addListener((observable, oldChanging, changing) -> {
						if(changing)
							item.sliderChangeStartValue = slider.getValue();
						else
							actionHistoryService.registerAction(new LayerSliderChangeAction(slider.getValue(), item.sliderChangeStartValue, slider::setValue));
					});
					vBox.getChildren().add(slider);
				}
				setGraphic(vBox);
			}

			private double getSliderConfigValue(String key, double defaultValue) {
				return key != null ? appConfig.getDouble(key) : defaultValue;
			}
		};
	}
}
