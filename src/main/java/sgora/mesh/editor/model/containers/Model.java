package sgora.mesh.editor.model.containers;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import sgora.mesh.editor.model.observables.ObservableProperty;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;

public class Model {

	public Point mainViewSize = new Point();

	public ImageBoxModel imageBoxModel = new ImageBoxModel();
	public MeshBoxModel meshBoxModel = new MeshBoxModel();

	public ObservableProperty<MouseTool> activeTool = new ObservableProperty<>(MouseTool.IMAGE_MOVER);
	public ObjectProperty<Cursor> mouseCursor;

	public String projectName = "Untitled";
	public ObservableProperty<Boolean> projectLoaded = new ObservableProperty<>(false);

}
