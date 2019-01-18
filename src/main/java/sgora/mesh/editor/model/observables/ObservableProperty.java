package sgora.mesh.editor.model.observables;

import java.util.function.UnaryOperator;

/**
 * Notifies listeners when model value is set
 */
public class ObservableProperty<T> extends SimpleObservable {

	private T modelValue;

	public ObservableProperty(T modelValue) {
		this.modelValue = modelValue;
	}

	public T get() {
		return modelValue;
	}

	public void set(T modelValue) {
		this.modelValue = modelValue;
		onValueChanged();
	}

	public void modify(UnaryOperator<T> operator) {
		set(operator.apply(modelValue));
	}

}
