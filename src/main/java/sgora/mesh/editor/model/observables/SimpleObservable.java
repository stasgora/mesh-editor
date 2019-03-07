package sgora.mesh.editor.model.observables;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds a list of listeners and notifies them of any change
 */
public abstract class SimpleObservable {

	protected transient Set<ChangeListener> listeners = new HashSet<>();

	public void addListener(ChangeListener callback) {
		listeners.add(callback);
	}

	protected void onValueChanged() {
		callListeners();
	}

	void callListeners() {
		listeners.forEach(ChangeListener::call);
	}

	public void copyListeners(SimpleObservable observable) {
		listeners.forEach(observable::addListener);
	}

	public void clearListeners() {
		listeners.clear();
	}

}
