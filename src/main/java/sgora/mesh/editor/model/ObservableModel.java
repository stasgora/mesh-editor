package sgora.mesh.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ObservableModel {

	private List<Consumer<ObservableModel>> setListeners = new ArrayList<>();
	private boolean wasValueSet = false;

	public void addSetListener(Consumer<ObservableModel> callback) {
		setListeners.add(callback);
	}

	public void notifyListeners() {
		if(wasValueSet) {
			setListeners.forEach(listener -> listener.accept(this));
			wasValueSet = false;
		}
	}

	protected void onValueSet() {
		wasValueSet = true;
	}

}
