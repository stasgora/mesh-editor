package stasgora.mesh.editor.services.files.workspace;

public enum FileChooserAction {
	OPEN_DIALOG("open"),
	SAVE_DIALOG("save");

	public String langKey;

	FileChooserAction(String langKey) {
		this.langKey = langKey;
	}
}
