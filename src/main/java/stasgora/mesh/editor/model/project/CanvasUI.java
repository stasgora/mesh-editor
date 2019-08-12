package stasgora.mesh.editor.model.project;

import com.google.inject.Singleton;
import io.github.stasgora.observetree.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import stasgora.mesh.editor.model.MouseConfig;
import stasgora.mesh.editor.model.geom.Point;

@Singleton
public class CanvasUI extends Observable {

	public Point canvasViewSize;
	public ObjectProperty<Cursor> canvasMouseCursor;
	public final MouseConfig mouseConfig = new MouseConfig();

}
