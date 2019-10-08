package dev.sgora.mesheditor.services.history.actions.property;

import dev.sgora.mesheditor.services.history.actions.UserAction;

import java.util.function.Consumer;

public class PropertyChangeAction<V> implements UserAction {
	private V newValue;
	private V oldValue;

	private Consumer<V> setValue;

	public PropertyChangeAction(V newValue, V oldValue, Consumer<V> setValue) {
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
