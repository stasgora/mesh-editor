package sgora.mesh.editor.ui.canvas;

import javafx.scene.image.Image;
import sgora.mesh.editor.model.geom.Rectangle;

public class ImageCanvas extends Canvas {

	public void draw(Rectangle imageBox, Image baseImage) {
		if(!isVisible()) {
			return;
		}
		context.drawImage(baseImage, imageBox.position.x, imageBox.position.y, imageBox.size.x,  imageBox.size.y);
	}
}
