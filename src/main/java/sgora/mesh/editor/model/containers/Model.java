package sgora.mesh.editor.model.containers;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import sgora.mesh.editor.model.observables.SettableProperty;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;

public class Model {

	public Point mainViewSize = new Point();

	public ImageBoxModel imageBoxModel = new ImageBoxModel();
	public MeshBoxModel meshBoxModel = new MeshBoxModel();
	public ProjectModel project = new ProjectModel();

	public SettableProperty<MouseTool> activeTool = new SettableProperty<>(MouseTool.IMAGE_MOVER);
	public ObjectProperty<Cursor> mouseCursor;

}
