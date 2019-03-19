package sgora.mesh.editor.model.project;

import sgora.mesh.editor.model.observables.Observable;
import sgora.mesh.editor.model.observables.SettableObservable;

public class Project extends Observable {

	public SettableObservable<LoadState> loadState = new SettableObservable<>(new LoadState());
	public SettableObservable<VisualProperties> visualProperties = new SettableObservable<>(new VisualProperties());
	public SettableObservable<CanvasData> canvasData = new SettableObservable<>(new CanvasData());

	public Project() {
		addSubObservable(loadState);
		addSubObservable(visualProperties);
		addSubObservable(canvasData);
	}

}
