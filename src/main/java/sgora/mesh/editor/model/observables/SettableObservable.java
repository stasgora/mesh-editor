package sgora.mesh.editor.model.observables;

import java.util.HashSet;
import java.util.Set;

/**
 * Maintains a set of observable listeners
 */
public class SettableObservable<T extends SimpleObservable> extends SettableProperty<T> {

	protected transient Set<ChangeListener> staticListeners = new HashSet<>();

	public SettableObservable() {
	}

	public SettableObservable(T modelValue) {
		super(modelValue);
	}

	public void addStaticListener(ChangeListener callback) {
		staticListeners.add(callback);
	}

	@Override
	public void set(T modelValue) {
		if(this.modelValue == modelValue) {
			return;
		}
		if(modelValue != null) {
			staticListeners.forEach(modelValue::addListener);
		}
		super.set(modelValue);
	}

}
