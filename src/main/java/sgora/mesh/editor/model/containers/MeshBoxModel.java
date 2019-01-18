package sgora.mesh.editor.model.containers;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import sgora.mesh.editor.model.geom.Mesh;

public class MeshBoxModel {

	public final Mesh mesh = new Mesh();
	public Color nodeColor = new Color(0.1, 0.2, 1, 1);
	public int nodeRadius = 8;

	public MouseButton placeNodeButton = MouseButton.PRIMARY;
	public MouseButton removeNodeButton = MouseButton.SECONDARY;
	public MouseButton moveNodeButton = MouseButton.PRIMARY;

}
