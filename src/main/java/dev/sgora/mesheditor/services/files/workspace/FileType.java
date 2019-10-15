package dev.sgora.mesheditor.services.files.workspace;

public enum FileType {
	PROJECT("project"),
	IMAGE("image"),
	EXPORT("export");

	public final String key;

	FileType(String key) {
		this.key = key;
	}
}
