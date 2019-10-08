package dev.sgora.mesheditor.model.project;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.MouseConfig;
import dev.sgora.mesheditor.model.geom.Point;
import dev.sgora.mesheditor.view.annotation.MainWindowStage;
import io.github.stasgora.observetree.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.stage.Stage;

@Singleton
public class CanvasUI extends Observable {

	public final Point canvasViewSize = new Point();
	public final ObjectProperty<Cursor> canvasMouseCursor;
	public final MouseConfig mouseConfig = new MouseConfig();

	@Inject
	CanvasUI(@MainWindowStage Stage window) {
		this.canvasMouseCursor = window.getScene().cursorProperty();
	}
}
