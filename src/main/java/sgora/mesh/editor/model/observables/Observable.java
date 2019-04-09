package sgora.mesh.editor.model.observables;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Holds a list of listeners which can be notified immediately or manually. Can be part of a tree structure.
 * Changes propagate up the hierarchy, listeners are called on all changed observables.
 */
public abstract class Observable {

	public transient boolean notifyManually = true;
	private transient boolean wasValueChanged = false;

	private transient Set<ChangeListener> listeners = new TreeSet<>();

	private transient Set<Observable> parents = new HashSet<>();
	private transient Set<Observable> children = new HashSet<>();

	private class ListenerEntry {
		public ChangeListener listener;
		public int priority;

		public ListenerEntry(ChangeListener listener, int priority) {
			this.listener = listener;
			this.priority = priority;
		}
	}

	public void addListener(ChangeListener callback) {
		addListener(callback, ListenerPriority.NORMAL);
	}

	public void addListener(ChangeListener callback, ListenerPriority priority) {
		addListener(callback, priority.value);
	}

	public void addListener(ChangeListener callback, int priority) {
		listeners.add(callback);
	}

	protected void addParent(Observable observable) {
		parents.add(observable);
	}

	protected void addChild(Observable observable) {
		children.add(observable);
	}

	protected void removeParent(Observable observable) {
		parents.remove(observable);
	}

	protected void removeChild(Observable observable) {
		children.remove(observable);
	}

	protected void addSubObservable(Observable observable) {
		initSubObservable(observable);
		if(observable instanceof SettableObservable) {
			SettableObservable settableObservable = (SettableObservable) observable;
			if(settableObservable.present()) {
				initSubObservable((Observable) settableObservable.modelValue);
			}
		}
	}

	protected void initSubObservable(Observable model) {
		model.addParent(this);
		addChild(model);
		model.setUnchanged();
	}

	protected void removeSubObservable(Observable model) {
		model.removeParent(this);
		removeChild(model);
	}

	protected void onValueChanged() {
		parents.forEach(Observable::onValueChanged);
		wasValueChanged = true;
		if (!notifyManually) {
			listeners.forEach(ChangeListener::call);
		}
	}

	protected void callListeners() {
		if(wasValueChanged && notifyManually) {
			wasValueChanged = false;
			listeners.forEach(ChangeListener::call);
		}
	}

	private void notifyParents() {
		callListeners();
		parents.forEach(Observable::notifyParents);
	}

	private void notifyChildren() {
		callListeners();
		children.forEach(Observable::notifyChildren);
	}

	public void notifyListeners() {
		notifyParents();
		notifyChildren();
	}

	public void copyListeners(Observable observable) {
		listeners.forEach(observable::addListener);
	}

	public void clearListeners() {
		listeners.clear();
	}

	public void setUnchanged() {
		wasValueChanged = false;
	}

}
