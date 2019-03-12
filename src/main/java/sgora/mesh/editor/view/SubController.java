package sgora.mesh.editor.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import sgora.mesh.editor.MeshEditor;
import sgora.mesh.editor.enums.SubView;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SubController {

	private static final Logger LOGGER = Logger.getLogger(SubController.class.getName());

	protected final Region root;
	private SubView subView;

	SubController(Region root, SubView subView) {
		this.root = root;
		this.subView = subView;
	}

	protected void loadView() {
		FXMLLoader loader = new FXMLLoader(MeshEditor.class.getResource("/fxml/" + subView.fxmlFileName + ".fxml"));
		loader.setRoot(root);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Loading " + subView.fxmlFileName + " component failed");
		}
		init();
	}

	 abstract void init();

}
