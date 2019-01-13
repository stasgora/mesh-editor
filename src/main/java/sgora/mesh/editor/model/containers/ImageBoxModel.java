package sgora.mesh.editor.model.containers;

import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import sgora.mesh.editor.model.geom.Rectangle;

public class ImageBoxModel {

	public final Rectangle imageBox = new Rectangle();
	public Image baseImage;

	public double zoomSpeed = 0.0025;
	public double dragSpeed = 1;
	public boolean dragging = false;
	public MouseButton dragButton = MouseButton.PRIMARY;

}
