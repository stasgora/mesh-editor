package sgora.mesh.editor.model;

/**
 * Notifies listeners when model value is set
 */
public class SimpleModelProperty<T> extends ObservableModel {

	private T modelValue;

	public SimpleModelProperty(T modelValue) {
		this.modelValue = modelValue;
	}

	public T getValue() {
		return modelValue;
	}

	public void setValue(T modelValue) {
		this.modelValue = modelValue;
		notifyListeners();
	}

}
