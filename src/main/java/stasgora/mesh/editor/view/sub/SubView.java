package stasgora.mesh.editor.view.sub;

import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.MeshEditor;
import stasgora.mesh.editor.model.NamespaceMap;
import stasgora.mesh.editor.view.ViewType;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SubView {

	private static final Logger LOGGER = Logger.getLogger(SubView.class.getName());

	protected final Region root;
	protected ViewType viewType;
	protected ObservableMap<String, Object> namespace;

	public SubView(Region root, ViewType viewType, NamespaceMap viewNamespaces) {
		this.root = root;
		this.viewType = viewType;
		loadView(viewNamespaces);
	}

	private void loadView(NamespaceMap viewNamespaces) {
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

	protected abstract void init();

}
