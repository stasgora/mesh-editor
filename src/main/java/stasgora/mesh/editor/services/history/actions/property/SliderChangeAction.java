package stasgora.mesh.editor.services.history.actions.property;

import java.util.function.Consumer;

public class SliderChangeAction extends PropertyChangeAction<Double> {

	public SliderChangeAction(Double newValue, Double oldValue, Consumer<Double> setValue) {
		super(newValue, oldValue, setValue);
	}

}
