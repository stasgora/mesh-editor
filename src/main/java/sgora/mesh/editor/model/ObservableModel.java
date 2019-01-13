package sgora.mesh.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Holds a list of listeners and notifies them all at once according to user's needs
 */
public abstract class ObservableModel {

	private List<Consumer<ObservableModel>> listeners = new ArrayList<>();
	private boolean wasValueChanged = false;

	public void addListener(Consumer<ObservableModel> callback) {
		listeners.add(callback);
	}

	public void notifyListeners() {
		if(wasValueChanged) {
			listeners.forEach(listener -> listener.accept(this));
			wasValueChanged = false;
		}
	}

	public void setUnchanged() {
		wasValueChanged = false;
	}

	protected void onValueChanged() {
		wasValueChanged = true;
	}

}
