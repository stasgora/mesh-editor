package stasgora.mesh.editor.services.files.workspace;

import javafx.stage.WindowEvent;

public interface WorkspaceAction {
	String getProjectName();

	void onNewProject();

	void onOpenProject();

	void onOpenRecentProject();

	void onCloseProject();

	void onSaveProject();

	void onSaveProjectAs();

	void onExportProject();

	void onWindowCloseRequest(WindowEvent event);

	void onExitApp();
}
