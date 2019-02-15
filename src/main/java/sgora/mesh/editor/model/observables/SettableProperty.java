package sgora.mesh.editor.model.observables;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * Notifies listeners when model value is set
 */
public class SettableProperty<T> extends SimpleObservable implements Serializable {

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
		if(this.modelValue == modelValue) {
			return;
		}
		this.modelValue = modelValue;
		onValueChanged();
	}

	public void modify(UnaryOperator<T> operator) {
		set(operator.apply(modelValue));
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(modelValue);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		modelValue = (T) in.readObject();
	}

}
