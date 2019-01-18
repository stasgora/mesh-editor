package sgora.mesh.editor.model.observables;

/**
 * Lets user control whether listener notifying is manual or automatic
 */
public abstract class ControlledObservable extends SimpleObservable {

	public boolean notifyManually = true;
	private boolean wasValueChanged = false;

	public void notifyListeners() {
		if(wasValueChanged) {
			callListeners();
			wasValueChanged = false;
		}
	}

	public void setUnchanged() {
		wasValueChanged = false;
	}

	@Override
	protected void onValueChanged() {
		if(notifyManually)
			wasValueChanged = true;
		else
			super.onValueChanged();
	}

}
