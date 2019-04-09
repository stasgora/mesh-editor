package sgora.mesh.editor.model.observables;

public enum ListenerPriority {

	VERY_HIGH(4),
	HIGH(3),
	NORMAL(2),
	LOW(1),
	VERY_LOW(0);

	public int value;

	ListenerPriority(int value) {
		this.value = value;
	}

}
