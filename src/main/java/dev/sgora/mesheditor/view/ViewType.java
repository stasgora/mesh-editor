package dev.sgora.mesheditor.view;

public enum ViewType {

	WINDOW_VIEW("window", "WindowView"),
	CANVAS_VIEW("canvas", "CanvasView"),
	MENU_VIEW("menu", "MenuView"),
	PROPERTIES_VIEW("properties", "PropertiesView");

	ViewType(String langPrefix, String fxmlFileName) {
		this.langPrefix = langPrefix;
		this.fxmlFileName = fxmlFileName;
	}

	public final String langPrefix;
	public final String fxmlFileName;

}
