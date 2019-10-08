package dev.sgora.mesheditor.services.files.workspace.interfaces;

import javafx.stage.WindowEvent;

import java.io.File;

public interface WorkspaceAction {
	String getProjectName();

	void onNewProject();

	void onOpenProject();

	void onOpenRecentProject(File project);

	void onCloseProject();

	void onSaveProject();

	void onSaveProjectAs();

	void onExportProject();

	void onWindowCloseRequest(WindowEvent event);

	void onExitApp();
}
