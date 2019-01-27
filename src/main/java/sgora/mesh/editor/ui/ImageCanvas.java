package sgora.mesh.editor.ui;

import sgora.mesh.editor.model.containers.ImageBoxModel;

public class ImageCanvas extends Canvas {

	public void draw(ImageBoxModel imageBox, boolean projectLoaded) {
		context.clearRect(0, 0, getWidth(), getHeight());
		if(!projectLoaded || !isVisible())
			return;
		context.drawImage(imageBox.baseImage, imageBox.imageBox.position.x, imageBox.imageBox.position.y, imageBox.imageBox.size.x, imageBox.imageBox.size.y);
	}

}
