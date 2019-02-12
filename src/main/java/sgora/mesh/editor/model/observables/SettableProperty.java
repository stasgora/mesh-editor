package sgora.mesh.editor.model.observables;

import java.util.function.UnaryOperator;

/**
 * Notifies listeners when model value is set
 */
public class SettableProperty<T> extends SimpleObservable {

	protected T modelValue;

	public SettableProperty() {
	}

	public SettableProperty(T modelValue) {
		this.modelValue = modelValue;
	}

	public T get() {
		return modelValue;
	}

	public void set(T modelValue) {
		if(this.modelValue == modelValue)
			return;
		this.modelValue = modelValue;
		onValueChanged();
	}

	public void modify(UnaryOperator<T> operator) {
		set(operator.apply(modelValue));
	}

}