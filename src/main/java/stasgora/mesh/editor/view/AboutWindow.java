package stasgora.mesh.editor.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
import javafx.stage.Stage;
import stasgora.mesh.editor.MeshEditor;
import stasgora.mesh.editor.services.config.AppConfigReader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutWindow {
	private final Logger LOGGER = Logger.getLogger(getClass().getName());

	private final Popup aboutWindow = new Popup();
	private final Stage mainWindow;
	public ImageView logo;

	public AboutWindow(Stage mainWindow, AppConfigReader appConfig) {
		this.mainWindow = mainWindow;

		FXMLLoader loader = new FXMLLoader(MeshEditor.class.getResource("/fxml/AboutWindow.fxml"));
		loader.setController(this);
		loader.getNamespace().put("app_version", appConfig.getString("app.version"));
		loader.getNamespace().put("app_name", appConfig.getString("app.name"));
		try {
			aboutWindow.getContent().add(loader.load());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Loading about page failed", e);
		}
		mainWindow.focusedProperty().addListener(((observable, oldValue, newValue) -> aboutWindow.hide()));
		aboutWindow.setAutoHide(true);

		logo.setImage(new Image(MeshEditor.class.getResourceAsStream("/logo.png")));
	}

	public void show() {
		aboutWindow.show(mainWindow);
		aboutWindow.centerOnScreen();
		aboutWindow.requestFocus();
	}
}
