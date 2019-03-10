package sgora.mesh.editor.interfaces.files;

import javafx.stage.WindowEvent;

public interface WorkspaceAction {
	String getProjectName();

	void onNewProject();

	void onOpenProject();

	void onOpenRecentProject();

	void onCloseProject();

	void onSaveProject();

	void onSaveProjectAs();

	void onWindowCloseRequest(WindowEvent event);

	void onExitApp();
}
