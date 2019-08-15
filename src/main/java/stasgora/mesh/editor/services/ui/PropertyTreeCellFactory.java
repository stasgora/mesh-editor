package stasgora.mesh.editor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import stasgora.mesh.editor.model.TextKeyProvider;
import stasgora.mesh.editor.model.observables.BindableProperty;
import stasgora.mesh.editor.model.project.MeshLayer;
import stasgora.mesh.editor.model.project.VisualProperties;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.LangConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.history.ActionHistoryService;
import stasgora.mesh.editor.services.history.actions.property.CheckBoxChangeAction;
import stasgora.mesh.editor.services.history.actions.property.PropertyChangeAction;
import stasgora.mesh.editor.ui.properties.PropertyItemType;
import stasgora.mesh.editor.ui.properties.PropertyTreeItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Singleton
public class PropertyTreeCellFactory implements Callback<TreeView<String>, TreeCell<String>> {

	private LangConfigReader appLang;
	private AppConfigReader appConfig;
	private VisualProperties visualProperties;
	private ActionHistoryService actionHistoryService;

	private Map<PropertyItemType, Function<PropertyTreeItem, BindableProperty>> propertyTypeToVisibleValue, propertyTypeToSliderValue, propertyTypeToComboBoxValue;

	@Inject
	PropertyTreeCellFactory(LangConfigReader appLang, @AppConfig AppConfigReader appConfig, VisualProperties visualProperties, ActionHistoryService actionHistoryService) {
		this.appLang = appLang;
		this.appConfig = appConfig;
		this.visualProperties = visualProperties;
		this.actionHistoryService = actionHistoryService;

		initPropertyMaps();
	}

	private void initPropertyMaps() {
		propertyTypeToVisibleValue = Map.of(
				PropertyItemType.IMAGE, item -> visualProperties.imageVisible,
				PropertyItemType.MESH, item -> visualProperties.meshVisible,
				PropertyItemType.TRIANGULATION, item -> visualProperties.triangulationLayer.get().layerVisible,
				PropertyItemType.VORONOI_DIAGRAM, item -> visualProperties.voronoiDiagramLayer.get().layerVisible,
				PropertyItemType.POLYGONS, item -> getPropertyLayer(item).polygonsVisible,
				PropertyItemType.NODES, item -> getPropertyLayer(item).nodesVisible,
				PropertyItemType.EDGES, item -> getPropertyLayer(item).edgesVisible
		);
		propertyTypeToSliderValue = Map.of(
				PropertyItemType.IMAGE, item -> visualProperties.imageTransparency,
				PropertyItemType.MESH, item -> visualProperties.meshTransparency,
				PropertyItemType.TRIANGULATION, item -> visualProperties.triangulationLayer.get().layerTransparency,
				PropertyItemType.VORONOI_DIAGRAM, item -> visualProperties.voronoiDiagramLayer.get().layerTransparency,
				PropertyItemType.NODES, item -> getPropertyLayer(item).nodeRadius,
				PropertyItemType.EDGES, item -> getPropertyLayer(item).edgeThickness
		);
		propertyTypeToComboBoxValue = new HashMap<>();
	}

	private MeshLayer getPropertyLayer(PropertyTreeItem item) {
		return ((PropertyTreeItem) item.getParent()).itemType == PropertyItemType.TRIANGULATION ?
				visualProperties.triangulationLayer.get() : visualProperties.voronoiDiagramLayer.get();
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

				if (treeItem.hasCheckBox)
					addCheckBox(treeItem, body);
				if (treeItem.hasSlider)
					addSlider(treeItem, body);
				//if (treeItem.hasComboBox)
				//	addComboBox(treeItem, body);
				setGraphic(body);
			}

			private void addCheckBox(PropertyTreeItem treeItem, HBox body) {
				CheckBox checkBox = new CheckBox();
				checkBox.setOnAction(event -> actionHistoryService.registerAction(new CheckBoxChangeAction(checkBox.isSelected(), checkBox::setSelected)));
				checkBox.setTooltip(new Tooltip(appLang.getText("fxml.properties.tooltips.visibility")));
				propertyTypeToVisibleValue.get(treeItem.itemType).apply(treeItem).bindWithFxObservable(checkBox.selectedProperty());
				body.getChildren().add(0, checkBox);
			}

			private void addSlider(PropertyTreeItem treeItem, HBox body) {
				double minValue = getSliderConfigValue(treeItem.itemType.getMinValueKey(), 0);
				Slider slider = new Slider(minValue, getSliderConfigValue(treeItem.itemType.getMaxValueKey(), 1), minValue);
				slider.setTooltip(new Tooltip(appLang.getText(treeItem.itemType.getSliderKey())));
				propertyTypeToSliderValue.get(treeItem.itemType).apply(treeItem).bindWithFxObservable(slider.valueProperty());

				slider.valueChangingProperty().addListener((observable, oldChanging, changing) -> {
					if (changing)
						treeItem.sliderChangeStartValue = slider.getValue();
					else
						actionHistoryService.registerAction(new PropertyChangeAction<>(slider.getValue(), treeItem.sliderChangeStartValue, slider::setValue));
				});
				body.getChildren().add(slider);
			}

			private <T extends Enum<T> & TextKeyProvider> void addComboBox(Class<T> enumType, PropertyTreeItem treeItem, HBox body) {
				ComboBox<T> comboBox = new ComboBox<>();
				comboBox.setItems(FXCollections.observableArrayList(enumType.getEnumConstants()));
				comboBox.setConverter(new StringConverter<>() {
					@Override
					public String toString(T meshType) {
						return appLang.getText(meshType.getTextKey());
					}

					@Override
					public T fromString(String s) {
						return Arrays.stream(enumType.getEnumConstants()).filter(type -> type.name().equals(s)).findFirst().orElse(null);
					}
				});
				propertyTypeToComboBoxValue.get(treeItem.itemType).apply(treeItem).bindWithFxObservable(comboBox.valueProperty());

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
