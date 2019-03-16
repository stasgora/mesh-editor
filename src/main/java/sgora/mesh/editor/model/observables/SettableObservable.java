package sgora.mesh.editor.model.observables;

import java.util.HashSet;
import java.util.Set;

/**
 * Maintains a set of observable listeners and parents for settable property
 */
public class SettableObservable<T extends Observable> extends SettableProperty<T> {

	protected transient Set<ChangeListener> staticListeners = new HashSet<>();
	protected transient Set<Observable> staticParents = new HashSet<>();

	public SettableObservable() {
	}

	public SettableObservable(T modelValue) {
		super(modelValue);
	}

	public void addStaticListener(ChangeListener callback) {
		staticListeners.add(callback);
	}

	@Override
	protected void addParent(Observable observable) {
		super.addParent(observable);
		staticParents.add(observable);
	}

	@Override
	public void set(T modelValue) {
		if(this.modelValue == modelValue) {
			return;
		}
		if(modelValue != null) {
			staticListeners.forEach(modelValue::addListener);
			staticParents.forEach(parent -> parent.addSubObservable(modelValue));
		}
		if(this.modelValue != null) {
			staticParents.forEach(parent -> parent.removeSubObservable(this.modelValue));
		}
		super.set(modelValue);
	}

}
