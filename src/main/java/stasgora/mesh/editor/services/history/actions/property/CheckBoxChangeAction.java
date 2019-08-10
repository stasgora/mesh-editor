package stasgora.mesh.editor.services.history.actions.property;

import java.util.function.Consumer;

public class CheckBoxChangeAction extends PropertyChangeAction<Boolean> {

	public CheckBoxChangeAction(Boolean newValue, Consumer<Boolean> setValue) {
		super(newValue, !newValue, setValue);
	}

}
