package stasgora.mesh.editor.services.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import stasgora.mesh.editor.model.project.MeshType;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.LangConfigReader;
import stasgora.mesh.editor.services.history.ActionHistoryService;
import stasgora.mesh.editor.services.history.actions.property.CheckBoxChangeAction;
import stasgora.mesh.editor.services.history.actions.property.PropertyChangeAction;
import stasgora.mesh.editor.ui.properties.PropertyTreeItem;

import java.util.Arrays;

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
		return new TreeCell<>() {
			@Override
			public void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);
				if (empty) {
					setGraphic(null);
					setText(null);
					return;
				}
				if (!(getTreeItem() instanceof PropertyTreeItem)) {
					setText(value);
					return;
				}
				PropertyTreeItem treeItem = (PropertyTreeItem) getTreeItem();
				HBox body = new HBox(new Label(appLang.getText(treeItem.getItemType().getTextKey())));
				setText(null);
				body.setSpacing(5);
				body.setAlignment(Pos.CENTER_LEFT);

				if(treeItem.hasCheckBox)
					addCheckBox(treeItem, body);
				if(treeItem.hasSlider)
					addSlider(treeItem, body);
				if(treeItem.hasComboBox)
					addComboBox(treeItem, body);
				setGraphic(body);
			}

			private void addCheckBox(PropertyTreeItem treeItem, HBox body) {
				CheckBox checkBox = new CheckBox();
				checkBox.setOnAction(event -> actionHistoryService.registerAction(new CheckBoxChangeAction(checkBox.isSelected(), checkBox::setSelected)));
				checkBox.setTooltip(new Tooltip(appLang.getText("fxml.properties.tooltips.visibility")));
				visualProperties.propertyTypeToVisibleValue.get(treeItem.itemType).bindWithFxObservable(checkBox.selectedProperty());
				body.getChildren().add(0, checkBox);
			}

			private void addSlider(PropertyTreeItem treeItem, HBox body) {
				double minValue = getSliderConfigValue(treeItem.itemType.getMinValueKey(), 0);
				Slider slider = new Slider(minValue, getSliderConfigValue(treeItem.itemType.getMaxValueKey(), 1), minValue);
				slider.setTooltip(new Tooltip(appLang.getText(treeItem.itemType.getSliderKey())));
				visualProperties.propertyTypeToSliderValue.get(treeItem.itemType).bindWithFxObservable(slider.valueProperty());

				slider.valueChangingProperty().addListener((observable, oldChanging, changing) -> {
					if(changing)
						treeItem.sliderChangeStartValue = slider.getValue();
					else
						actionHistoryService.registerAction(new PropertyChangeAction<>(slider.getValue(), treeItem.sliderChangeStartValue, slider::setValue));
				});
				body.getChildren().add(slider);
			}

			private void addComboBox(PropertyTreeItem treeItem, HBox body) {
				ComboBox<MeshType> comboBox = new ComboBox<>();
				comboBox.setItems(FXCollections.observableArrayList(MeshType.values()));
				comboBox.setConverter(new StringConverter<>() {
					@Override
					public String toString(MeshType meshType) {
						return appLang.getText(meshType.getTextKey());
					}

					@Override
					public MeshType fromString(String s) {
						return Arrays.stream(MeshType.values()).filter(type -> type.name().equals(s)).findFirst().orElse(null);
					}
				});
				comboBox.setValue(MeshType.TRIANGULATION);
				visualProperties.propertyTypeToComboBoxValue.get(treeItem.itemType).bindWithFxObservable(comboBox.valueProperty());

				//comboBox.valueProperty().addListener((observable, oldVal, newVal) -> actionHistoryService.registerAction(
				//		new PropertyChangeAction<>(newVal, oldVal, comboBox::setValue))); // TODO register mesh type changes in action history (currently self recursive)
				body.getChildren().add(comboBox);
			}

			private double getSliderConfigValue(String key, double defaultValue) {
				return key != null ? appConfig.getDouble(key) : defaultValue;
			}
		};
	}
}
