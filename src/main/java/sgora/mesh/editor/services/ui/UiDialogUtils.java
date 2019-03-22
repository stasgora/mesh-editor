package sgora.mesh.editor.services.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sgora.mesh.editor.enums.FileChooserAction;
import sgora.mesh.editor.interfaces.config.LangConfigReader;

import java.io.File;
import java.util.Optional;

public class UiDialogUtils {

	private Stage window;
	private LangConfigReader appLang;

	public UiDialogUtils(Stage window, LangConfigReader appLang) {
		this.window = window;
		this.appLang = appLang;
	}

	public File showFileChooser(FileChooserAction action, String title, FileChooser.ExtensionFilter extensionFilter) {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle(title);
		projectFileChooser.getExtensionFilters().addAll(extensionFilter, getDefaultFilter());
		if(action == FileChooserAction.SAVE_DIALOG) {
			return projectFileChooser.showSaveDialog(window);
		} else if(action == FileChooserAction.OPEN_DIALOG) {
			return projectFileChooser.showOpenDialog(window);
		}
		return null;
	}

	public Optional<ButtonType> showWarningDialog(String title, String header, String content, ButtonType[] buttons) {
		Alert dialog = new Alert(Alert.AlertType.WARNING, content, buttons);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		return dialog.showAndWait();
	}

	private FileChooser.ExtensionFilter getDefaultFilter() {
		String extensionTitle = appLang.getText("dialog.fileChooser.extension.all");
		return new FileChooser.ExtensionFilter(extensionTitle, "*.*");
	}

}
