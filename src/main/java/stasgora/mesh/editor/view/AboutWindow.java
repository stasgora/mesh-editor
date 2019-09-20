package stasgora.mesh.editor.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
import javafx.stage.Stage;
import stasgora.mesh.editor.MeshEditor;
import stasgora.mesh.editor.services.config.AppConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.view.annotation.MainWindowStage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class AboutWindow {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final Popup aboutWindowPopup = new Popup();
	private final Stage mainWindow;
	@FXML
	private ImageView logo;

	@Inject
	AboutWindow(@MainWindowStage Stage mainWindow, @AppConfig AppConfigReader appConfig) {
		this.mainWindow = mainWindow;

		FXMLLoader loader = new FXMLLoader(MeshEditor.class.getResource("/fxml/AboutWindow.fxml"));
		loader.setController(this);
		loader.getNamespace().put("app_version", appConfig.getString("app.version"));
		loader.getNamespace().put("app_name", appConfig.getString("app.name"));
		try {
			aboutWindowPopup.getContent().add(loader.load());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Loading about page failed", e);
		}
		mainWindow.focusedProperty().addListener(((observable, oldValue, newValue) -> aboutWindowPopup.hide()));
		aboutWindowPopup.setAutoHide(true);

		logo.setImage(new Image(MeshEditor.class.getResourceAsStream("/logo.png")));
	}

	public void show() {
		aboutWindowPopup.show(mainWindow);
		aboutWindowPopup.centerOnScreen();
		aboutWindowPopup.requestFocus();
	}
}
