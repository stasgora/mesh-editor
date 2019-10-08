package dev.sgora.mesheditor.services.files.workspace;

public enum FileChooserAction {
	OPEN_DIALOG("open"),
	SAVE_DIALOG("save");

	public final String langKey;

	FileChooserAction(String langKey) {
		this.langKey = langKey;
	}
}
