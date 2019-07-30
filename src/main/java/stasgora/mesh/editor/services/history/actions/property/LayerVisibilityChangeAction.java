package stasgora.mesh.editor.services.history.actions.property;

import java.util.function.Consumer;

public class LayerVisibilityChangeAction extends LayerPropertyChangeAction<Boolean> {

	public LayerVisibilityChangeAction(Boolean newValue, Consumer<Boolean> setValue) {
		super(newValue, !newValue, setValue);
	}

}
