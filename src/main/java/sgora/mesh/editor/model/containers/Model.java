package sgora.mesh.editor.model.containers;

import sgora.mesh.editor.model.SimpleModelProperty;
import sgora.mesh.editor.model.geom.Point;
import sgora.mesh.editor.model.input.MouseTool;

public class Model {

	public Point mainViewSize = new Point();

	public ImageBoxModel imageBoxModel = new ImageBoxModel();
	public MeshBoxModel meshBoxModel = new MeshBoxModel();

	public SimpleModelProperty<MouseTool> activeTool = new SimpleModelProperty<>(MouseTool.IMAGE_MOVER);

}
