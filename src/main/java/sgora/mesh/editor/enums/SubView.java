package sgora.mesh.editor.enums;

public enum SubView {

	CANVAS_VIEW("canvas", "CanvasView"),
	MENU_VIEW("menu", "MenuView"),
	PROPERTIES_VIEW("props", "PropertiesView");

	SubView(String langPrefix, String fxmlFileName) {
		this.langPrefix = langPrefix;
		this.fxmlFileName = fxmlFileName;
	}

	public String langPrefix, fxmlFileName;

}
