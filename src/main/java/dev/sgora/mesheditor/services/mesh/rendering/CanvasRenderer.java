package dev.sgora.mesheditor.services.mesh.rendering;

import javafx.scene.canvas.GraphicsContext;
import dev.sgora.mesheditor.model.geom.polygons.Rectangle;

public interface CanvasRenderer {
	void render();
	void drawBoundingBox(Rectangle boundingBox);
	void setContext(GraphicsContext context);
}
