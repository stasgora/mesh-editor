package sgora.mesh.editor.model.observables;

/**
 * Notifies listeners when model value is set
 */
public class SimpleModelProperty<T> extends SimpleObservable {

	private T modelValue;

	public SimpleModelProperty(T modelValue) {
		this.modelValue = modelValue;
	}

	public T getValue() {
		return modelValue;
	}

	public void setValue(T modelValue) {
		this.modelValue = modelValue;
		onValueChanged();
	}

}
