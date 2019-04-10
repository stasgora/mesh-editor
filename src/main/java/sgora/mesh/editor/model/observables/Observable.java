package sgora.mesh.editor.model.observables;

import sgora.mesh.editor.model.observables.listeners.ChangeListener;
import sgora.mesh.editor.model.observables.listeners.ListenerEntry;
import sgora.mesh.editor.model.observables.listeners.ListenerPriority;

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

	private transient Set<ListenerEntry> listeners = new TreeSet<>();

	private transient Set<Observable> parents = new HashSet<>();
	private transient Set<Observable> children = new HashSet<>();

	public void addListener(ChangeListener callback) {
		addListener(callback, ListenerPriority.NORMAL);
	}

	public void addListener(ChangeListener callback, ListenerPriority priority) {
		addListener(callback, priority.value);
	}

	public void addListener(ChangeListener callback, int priority) {
		listeners.add(new ListenerEntry(callback, priority));
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
			listeners.forEach(entry -> entry.listener.call());
		}
	}

	private Set<ListenerEntry> collectListeners(boolean upDir) {
		Set<ListenerEntry> treeListeners = new TreeSet<>();
		if(wasValueChanged && notifyManually) {
			wasValueChanged = false;
			treeListeners.addAll(listeners);
		}
		Set<Observable> relatives = upDir ? parents : children;
		relatives.forEach(observable -> treeListeners.addAll(observable.collectListeners(upDir)));
		return treeListeners;
	}

	public void notifyListeners() {
		Set<ListenerEntry> treeListeners = collectListeners(true);
		treeListeners.addAll(collectListeners(false));
		treeListeners.forEach(entry -> entry.listener.call());
	}

	public void copyListeners(Observable observable) {
		listeners.forEach(listener -> observable.addListener(listener.listener, listener.priority));
	}

	public void clearListeners() {
		listeners.clear();
	}

	public void setUnchanged() {
		setUnchanged(true);
		setUnchanged(false);
	}

	private void setUnchanged(boolean upDir) {
		wasValueChanged = false;
		Set<Observable> relatives = upDir ? parents : children;
		relatives.forEach(relative -> relative.setUnchanged(upDir));
	}

}
