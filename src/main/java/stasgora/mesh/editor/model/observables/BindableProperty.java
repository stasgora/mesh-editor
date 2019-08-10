package stasgora.mesh.editor.model.observables;

import io.github.stasgora.observetree.SettableProperty;
import io.github.stasgora.observetree.listener.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

import java.util.function.Function;

public class BindableProperty<T> extends SettableProperty<T> {

	public BindableProperty() {
	}

	public BindableProperty(T modelValue) {
		super(modelValue);
	}

	public <O extends WritableValue<T> & ObservableValue<T>> void bindWithFxObservable(O observable) {
		bindWithFxObservable(observable, val -> val, val -> val);
	}

	public <S, O extends WritableValue<S> & ObservableValue<S>> void bindWithFxObservable(O observable, Function<T, S> toFxObservable, Function<S, T> fromFxObservable) {
		observable.addListener((observableValue, oldVal, newVal) -> setAndNotify(fromFxObservable.apply(newVal)));
		ChangeListener setFxObservable = () -> observable.setValue(toFxObservable.apply(modelValue));
		addListener(setFxObservable);
		if (modelValue != null)
			setFxObservable.call();
	}

}
