package stasgora.mesh.editor.services.history.actions.property;

import java.util.function.Consumer;

public class LayerSliderChangeAction extends LayerPropertyChangeAction<Double> {

	public LayerSliderChangeAction(Double newValue, Double oldValue, Consumer<Double> setValue) {
		super(newValue, oldValue, setValue);
	}

}
