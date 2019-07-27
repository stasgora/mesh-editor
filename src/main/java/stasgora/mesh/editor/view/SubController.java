package stasgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.MeshEditor;
import stasgora.mesh.editor.enums.ViewType;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SubController {

	private static final Logger LOGGER = Logger.getLogger(SubController.class.getName());

	protected final Region root;
	protected ViewType viewType;
	protected ObservableMap<String, Object> namespace;

	SubController(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces) {
		this.root = root;
		this.viewType = viewType;
		loadView(viewNamespaces);
	}

	public void loadView(Map<String, ObservableMap<String, Object>> viewNamespaces) {
		FXMLLoader loader = new FXMLLoader(MeshEditor.class.getResource("/fxml/" + viewType.fxmlFileName + ".fxml"));
		loader.setRoot(root);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Loading " + viewType.fxmlFileName + " component failed", e);
		}
		namespace = loader.getNamespace();
		viewNamespaces.put(viewType.langPrefix, namespace);
	}

	 abstract void init();

}
