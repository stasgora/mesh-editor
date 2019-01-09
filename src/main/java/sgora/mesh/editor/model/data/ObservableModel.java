package sgora.mesh.editor.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

	protected void onValueChanged() {
		wasValueChanged = true;
	}

}
