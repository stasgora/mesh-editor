package sgora.mesh.editor.model.observables;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

public class BindableProperty<T> extends SettableProperty<T> {

	public BindableProperty() {
	}

	public BindableProperty(T modelValue) {
		super(modelValue);
	}

	public <O extends WritableValue<T> & ObservableValue<T>> void bindWithFxObservable(O observable) {
		observable.addListener((observableValue, oldVal, newVal) -> setAndNotify(newVal));
		addListener(() -> observable.setValue(modelValue));
	}

}
