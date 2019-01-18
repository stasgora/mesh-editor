package sgora.mesh.editor.model.observables;

/**
 * Forwards changes to it's sub-observables up the hierarchy
 */
public abstract class ComplexObservable extends ControlledObservable {

	protected void addSubObservable(SimpleObservable model) {
		model.addListener(this::onSubObservableChange);
	}

	private void onSubObservableChange() {
		this.onValueChanged();
	}

}
