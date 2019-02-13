package sgora.mesh.editor.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.enums.MouseTool;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MainToolBar extends ToolBar {

	private SettableProperty<MouseTool> mouseToolProperty;

	public void init(SettableProperty<MouseTool> mouseToolProperty) {
		this.mouseToolProperty = mouseToolProperty;
		Arrays.asList(MouseTool.values()).forEach(this::addTool);
	}

	private void addTool(MouseTool tool) {
		Button toolButton = new Button(Arrays.stream(tool.name().split("_")).map(this::formatWord).collect(Collectors.joining(" ")));
		toolButton.setUserData(tool);
		toolButton.onActionProperty().setValue(this::onToolChosen);
		getItems().add(toolButton);
	}

	private String formatWord(String word) {
		return word.substring(0, 1) + word.substring(1).toLowerCase();
	}

	private void onToolChosen(ActionEvent event) {
		mouseToolProperty.set((MouseTool) ((Button) event.getSource()).getUserData());
	}

}
