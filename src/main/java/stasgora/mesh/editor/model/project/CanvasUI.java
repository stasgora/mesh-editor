package stasgora.mesh.editor.model.project;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.stasgora.observetree.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.stage.Stage;
import stasgora.mesh.editor.model.MouseConfig;
import stasgora.mesh.editor.model.geom.Point;
import stasgora.mesh.editor.view.annotation.MainWindowStage;

@Singleton
public class CanvasUI extends Observable {

	public Point canvasViewSize = new Point();
	public ObjectProperty<Cursor> canvasMouseCursor;
	public final MouseConfig mouseConfig = new MouseConfig();

	@Inject
	CanvasUI(@MainWindowStage Stage window) {
		this.canvasMouseCursor = window.getScene().cursorProperty();
	}
}
