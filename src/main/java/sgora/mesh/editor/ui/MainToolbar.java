package sgora.mesh.editor.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import sgora.mesh.editor.interfaces.config.LangConfigReader;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.enums.MouseTool;

import java.util.Arrays;

public class MainToolbar extends ToolBar {

	private SettableProperty<MouseTool> mouseToolProperty;
	private LangConfigReader appLang;

	public void init(SettableProperty<MouseTool> mouseToolProperty, LangConfigReader appLang) {
		this.mouseToolProperty = mouseToolProperty;
		this.appLang = appLang;
		Arrays.asList(MouseTool.values()).forEach(this::addTool);
	}

	private void addTool(MouseTool tool) {
		Button toolButton = new Button(appLang.getText("tool." + tool.langKey));
		toolButton.setUserData(tool);
		toolButton.onActionProperty().setValue(this::onToolChosen);
		getItems().add(toolButton);
	}

	private void onToolChosen(ActionEvent event) {
		mouseToolProperty.set((MouseTool) ((Button) event.getSource()).getUserData());
	}

}
