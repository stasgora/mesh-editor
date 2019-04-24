package sgora.mesh.editor.model.project;

import io.github.stasgora.observetree.Observable;

public class Project extends Observable {

	public LoadState loadState = new LoadState();
	public VisualProperties visualProperties = new VisualProperties();
	public CanvasData canvasData = new CanvasData();

	public Project() {
		addSubObservable(loadState);
		addSubObservable(visualProperties);
		addSubObservable(canvasData);
	}

}
