package sgora.mesh.editor.ui;

import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.geom.Rectangle;

public class ImageCanvas extends Canvas {

	public void draw(Model model) {
		if(!isVisible())
			return;
		Rectangle imageBox = model.imageBoxModel.imageBox;
		context.drawImage(model.project.baseImage.get(), imageBox.position.x, imageBox.position.y, imageBox.size.x,  imageBox.size.y);
	}

}
