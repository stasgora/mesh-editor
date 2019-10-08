package dev.sgora.mesheditor.services.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import dev.sgora.mesheditor.services.config.LangConfigReader;
import dev.sgora.mesheditor.services.files.workspace.FileChooserAction;
import dev.sgora.mesheditor.view.annotation.MainWindowStage;

import java.io.File;
import java.util.Optional;

@Singleton
public class UiDialogUtils {

	private Stage window;
	private LangConfigReader appLang;

	private EventHandler<KeyEvent> pressOnEnter = event -> {
		if (KeyCode.ENTER.equals(event.getCode()) && event.getTarget() instanceof Button) {
			((Button) event.getTarget()).fire();
		}
	};

	@Inject
	UiDialogUtils(@MainWindowStage Stage window, LangConfigReader appLang) {
		this.window = window;
		this.appLang = appLang;
	}

	public File showFileChooser(FileChooserAction action, String title, FileChooser.ExtensionFilter extensionFilter) {
		FileChooser projectFileChooser = new FileChooser();
		projectFileChooser.setTitle(title);
		projectFileChooser.getExtensionFilters().addAll(extensionFilter, getDefaultFilter());
		if (action == FileChooserAction.SAVE_DIALOG) {
			return projectFileChooser.showSaveDialog(window);
		} else if (action == FileChooserAction.OPEN_DIALOG) {
			return projectFileChooser.showOpenDialog(window);
		}
		return null;
	}

	public Optional<ButtonType> showWarningDialog(String title, String header, String content, ButtonType[] buttons) {
		return showDialog(Alert.AlertType.WARNING, title, header, content, buttons);
	}

	public Optional<ButtonType> showErrorDialog(String title, String header, String content) {
		return showDialog(Alert.AlertType.ERROR, title, header, content, null);
	}

	private Optional<ButtonType> showDialog(Alert.AlertType type, String title, String header, String content, ButtonType[] buttons) {
		Alert dialog;
		if (buttons != null) {
			dialog = new Alert(type, content, buttons);
		} else {
			dialog = new Alert(type, content);
		}
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getButtonTypes().stream().map(dialogPane::lookupButton).forEach(button -> button.addEventHandler(KeyEvent.KEY_PRESSED, pressOnEnter));
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		return dialog.showAndWait();
	}

	private FileChooser.ExtensionFilter getDefaultFilter() {
		String extensionTitle = appLang.getText("dialog.fileChooser.extension.all");
		return new FileChooser.ExtensionFilter(extensionTitle, "*.*");
	}

}
