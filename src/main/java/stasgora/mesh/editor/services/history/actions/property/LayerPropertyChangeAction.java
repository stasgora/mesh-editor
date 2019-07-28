package stasgora.mesh.editor.services.history.actions.property;

import stasgora.mesh.editor.interfaces.action.history.UserAction;

import java.util.function.Consumer;

public abstract class LayerPropertyChangeAction<V> implements UserAction {
	private V newValue;
	private V oldValue;

	private Consumer<V> setValue;

	public LayerPropertyChangeAction(V newValue, V oldValue, Consumer<V> setValue) {
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.setValue = setValue;
	}

	@Override
	public void execute() {
		setValue.accept(newValue);
	}

	@Override
	public void unExecute() {
		setValue.accept(oldValue);
	}
}
