package sgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import sgora.mesh.editor.MeshEditor;
import sgora.mesh.editor.enums.SubView;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SubController {

	private static final Logger LOGGER = Logger.getLogger(SubController.class.getName());

	protected final Region root;
	protected SubView subView;
	protected ObservableMap<String, Object> namespace;

	SubController(Region root, SubView subView, Map<SubView, ObservableMap<String, Object>> viewNamespaces) {
		this.root = root;
		this.subView = subView;
		loadView(viewNamespaces);
	}

	public void loadView(Map<SubView, ObservableMap<String, Object>> viewNamespaces) {
		FXMLLoader loader = new FXMLLoader(MeshEditor.class.getResource("/fxml/" + subView.fxmlFileName + ".fxml"));
		loader.setRoot(root);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Loading " + subView.fxmlFileName + " component failed");
		}
		namespace = loader.getNamespace();
		viewNamespaces.put(subView, namespace);
	}

	 abstract void init();

}
