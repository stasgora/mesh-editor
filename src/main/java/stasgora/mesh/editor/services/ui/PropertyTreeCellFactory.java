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
	private static final String PROPERTY_STYLE_CLASS = "property-item";

	private LangConfigReader appLang;
	private AppConfigReader appConfig;
	private VisualProperties visualProperties;
	private ActionHistoryService actionHistoryService;

	private Map<PropertyItemType, Function<PropertyTreeItem, BindableProperty>> propertyTypeToVisibleValue;
	private Map<PropertyItemType, Function<PropertyTreeItem, BindableProperty>> propertyTypeToSliderValue;
	private Map<PropertyItemType, Function<PropertyTreeItem, BindableProperty>> propertyTypeToComboBoxValue;

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
		return ((PropertyTreeItem) item.getParent()).getItemType() == PropertyItemType.TRIANGULATION ?
				visualProperties.triangulationLayer.get() : visualProperties.voronoiDiagramLayer.get();
	}

	@Override
	public TreeCell<String> call(TreeView<String> param) {
		return new TreeCell<>() {
			@Override
			public void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);
				getStyleClass().add(PROPERTY_STYLE_CLASS);
				if (empty) {
					setGraphic(null);
					setText(null);
					return;
				}
				String itemLabelText = value;
				if (getTreeItem() instanceof PropertyTreeItem)
					itemLabelText = appLang.getText(((PropertyTreeItem) getTreeItem()).getItemType().getTextKey());

				HBox body = new HBox(new Label(itemLabelText));
				setText(null);
				setGraphic(body);

				body.setSpacing(5);
				body.setAlignment(Pos.CENTER_LEFT);

				if (!(getTreeItem() instanceof PropertyTreeItem))
					return;

				PropertyTreeItem treeItem = (PropertyTreeItem) getTreeItem();
				if (treeItem.isHasCheckBox())
					addCheckBox(treeItem, body);
				if (treeItem.isHasSlider())
					addSlider(treeItem, body);
			}

			private void addCheckBox(PropertyTreeItem treeItem, HBox body) {
				CheckBox checkBox = new CheckBox();
				checkBox.setOnAction(event -> actionHistoryService.registerAction(new CheckBoxChangeAction(checkBox.isSelected(), checkBox::setSelected)));
				checkBox.setTooltip(new Tooltip(appLang.getText("fxml.properties.tooltips.visibility")));
				propertyTypeToVisibleValue.get(treeItem.getItemType()).apply(treeItem).bindWithFxObservable(checkBox.selectedProperty());
				body.getChildren().add(0, checkBox);
			}

			private void addSlider(PropertyTreeItem treeItem, HBox body) {
				double minValue = getSliderConfigValue(treeItem.getItemType().getMinValueKey(), 0);
				Slider slider = new Slider(minValue, getSliderConfigValue(treeItem.getItemType().getMaxValueKey(), 1), minValue);
				slider.setTooltip(new Tooltip(appLang.getText(treeItem.getItemType().getSliderKey())));
				propertyTypeToSliderValue.get(treeItem.getItemType()).apply(treeItem).bindWithFxObservable(slider.valueProperty());

				slider.valueChangingProperty().addListener((observable, oldChanging, changing) -> {
					if (changing)
						treeItem.setSliderChangeStartValue(slider.getValue());
					else
						actionHistoryService.registerAction(new PropertyChangeAction<>(slider.getValue(), treeItem.getSliderChangeStartValue(), slider::setValue));
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
				propertyTypeToComboBoxValue.get(treeItem.getItemType()).apply(treeItem).bindWithFxObservable(comboBox.valueProperty());

				body.getChildren().add(comboBox);
			}

			private double getSliderConfigValue(String key, double defaultValue) {
				return key != null ? appConfig.getDouble(key) : defaultValue;
			}
		};
	}
}
