package sgora.mesh.editor.model.observables;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * Notifies listeners when model value is set
 */
public class SettableProperty<T> extends Observable implements Serializable {

	protected T modelValue;

	private static final long serialVersionUID = 1L;

	public SettableProperty() {
	}

	public SettableProperty(T modelValue) {
		this.modelValue = modelValue;
	}

	public boolean present() {
		return modelValue != null;
	}

	public T get() {
		return modelValue;
	}

	public void set(T modelValue) {
		if(this.modelValue == modelValue) {
			return;
		}
		this.modelValue = modelValue;
		onValueChanged();
	}

	public void setAndNotify(T modelValue) {
		set(modelValue);
		notifyListeners();
	}

	public void modify(UnaryOperator<T> operator) {
		set(operator.apply(modelValue));
	}

}
