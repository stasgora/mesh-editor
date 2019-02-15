package sgora.mesh.editor.enums;

public enum MouseTool {
	MESH_EDITOR("meshEditor"),
	IMAGE_MOVER("imageMover");

	public String langKey;

	MouseTool(String langKey) {
		this.langKey = langKey;
	}
}
