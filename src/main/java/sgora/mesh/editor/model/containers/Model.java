package sgora.mesh.editor.model.containers;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import sgora.mesh.editor.model.observables.ObservableProperty;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;

import java.io.File;

public class Model {

	public Point mainViewSize = new Point();

	public ImageBoxModel imageBoxModel = new ImageBoxModel();
	public MeshBoxModel meshBoxModel = new MeshBoxModel();

	public ObservableProperty<MouseTool> activeTool = new ObservableProperty<>(MouseTool.IMAGE_MOVER);
	public ObjectProperty<Cursor> mouseCursor;

	public ObservableProperty<String> projectName = new ObservableProperty<>("Untitled");
	public ObservableProperty<Boolean> projectLoaded = new ObservableProperty<>(false);
	public File projectFile;

}
