package stasgora.mesh.editor.services.mesh.rendering;

import javafx.scene.canvas.GraphicsContext;
import stasgora.mesh.editor.model.geom.polygons.Rectangle;

public interface CanvasRenderer {
	void render();
	void drawBoundingBox(Rectangle boundingBox);
	void setContext(GraphicsContext context);
}
