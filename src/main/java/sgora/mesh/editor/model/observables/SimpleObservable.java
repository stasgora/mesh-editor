package sgora.mesh.editor.model.observables;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of listeners and notifies them of any change
 */
public abstract class SimpleObservable {

	protected List<ChangeListener> listeners = new ArrayList<>();

	public void addListener(ChangeListener callback) {
		listeners.add(callback);
	}

	protected void onValueChanged() {
		callListeners();
	}

	void callListeners() {
		listeners.forEach(ChangeListener::call);
	}

}
