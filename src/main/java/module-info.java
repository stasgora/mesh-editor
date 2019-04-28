module mesh.editor {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.logging;
	requires org.json;
	requires org.jfree.jfreesvg;
	requires java.desktop;
	requires stasgora.observetree;

	opens stasgora.mesh.editor to javafx.fxml;
	exports stasgora.mesh.editor;
}