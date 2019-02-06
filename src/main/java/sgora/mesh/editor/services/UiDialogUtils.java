package sgora.mesh.editor.services;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.enums.FileChooserAction;

import java.io.File;
import java.util.Optional;

public class UiDialogUtils {

	private Stage stage;

	public UiDialogUtils(Stage stage) {
		this.stage = stage;
	}

	public File showFileChooser(FileChooserAction action, String title, FileChooser.ExtensionFilter extensionFilter) {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle(title);
		projectFileChooser.getExtensionFilters().add(extensionFilter);
		if(action == FileChooserAction.SAVE_DIALOG)
			return projectFileChooser.showSaveDialog(stage);
		else if(action == FileChooserAction.OPEN_DIALOG)
			return projectFileChooser.showOpenDialog(stage);
		return null;
	}

	public Optional<ButtonType> showWarningDialog(String title, String header, String content, ButtonType[] buttons) {
		Alert dialog = new Alert(Alert.AlertType.WARNING, content, buttons);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		return dialog.showAndWait();
	}

}
