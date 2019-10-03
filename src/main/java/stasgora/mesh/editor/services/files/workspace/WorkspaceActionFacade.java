package stasgora.mesh.editor.services.files.workspace;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import stasgora.mesh.editor.model.project.CanvasUI;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.config.LangConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.config.interfaces.AppConfigReader;
import stasgora.mesh.editor.services.files.FileUtils;
import stasgora.mesh.editor.services.files.ProjectIOException;
import stasgora.mesh.editor.services.files.workspace.interfaces.RecentProjectManager;
import stasgora.mesh.editor.services.files.workspace.interfaces.WorkspaceAction;
import stasgora.mesh.editor.services.ui.UiDialogUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
class WorkspaceActionFacade implements WorkspaceAction {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private final WorkspaceActionExecutor workspaceActionExecutor;
	private final LangConfigReader appLang;
	private final UiDialogUtils dialogUtils;
	private final FileUtils fileUtils;
	private final AppConfigReader appConfig;
	private final LoadState loadState;
	private final ObjectProperty<Cursor> mouseCursor;
	private final RecentProjectManager recentProjectManager;

	@Inject
	WorkspaceActionFacade(WorkspaceActionExecutor workspaceActionExecutor, LangConfigReader appLang, UiDialogUtils dialogUtils, FileUtils fileUtils,
	                      @AppConfig AppConfigReader appConfig, LoadState loadState, CanvasUI canvasUI, RecentProjectManager recentProjectManager) {
		this.workspaceActionExecutor = workspaceActionExecutor;
		this.appLang = appLang;
		this.dialogUtils = dialogUtils;
		this.fileUtils = fileUtils;
		this.appConfig = appConfig;
		this.loadState = loadState;
		this.mouseCursor = canvasUI.canvasMouseCursor;
		this.recentProjectManager = recentProjectManager;
	}

	@Override
	public String getProjectName() {
		String projectName;
		if (loadState.file.get() == null)
			projectName = loadState.loaded.get() ? appLang.getText("defaultProjectName") : null;
		else
			projectName = fileUtils.getProjectFileName(loadState.file.get());
		return projectName;
	}

	@Override
	public void onNewProject() {
		String title = appLang.getText("action.project.create");
		if (showConfirmDialog() && !confirmWorkspaceAction(title)) {
			return;
		}
		String[] imageTypes = appConfig.getStringList("supported.imageTypes").stream().map(item -> "*." + item).toArray(String[]::new);
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(appLang.getText("dialog.fileChooser.extension.image"), imageTypes);
		File location = dialogUtils.showFileChooser(FileChooserAction.OPEN_DIALOG, appLang.getText("action.project.new"), filter);
		if (location != null) {
			try {
				workspaceActionExecutor.createNewProject(location);
			} catch (ProjectIOException e) {
				logger.log(Level.SEVERE, "Failed creating new project at '" + location.getAbsolutePath() + "'", e);
				showErrorDialog(title);
			}
		}
	}

	@Override
	public void onOpenProject() {
		String title = appLang.getText("action.project.open");
		if (showConfirmDialog() && !confirmWorkspaceAction(title)) {
			return;
		}
		File location = showProjectFileChooser(FileChooserAction.OPEN_DIALOG);
		if (location != null)
			openProject(location, title);

	}

	@Override
	public void onOpenRecentProject(File project) {
		openProject(project, appLang.getText("action.project.open"));
	}

	@Override
	public void onCloseProject() {
		if (!showConfirmDialog() || confirmWorkspaceAction(appLang.getText("action.project.close"))) {
			workspaceActionExecutor.closeProject();
			mouseCursor.setValue(Cursor.DEFAULT);
		}
	}

	@Override
	public void onSaveProject() {
		saveProject(false);
	}

	@Override
	public void onSaveProjectAs() {
		saveProject(true);
	}

	@Override
	public void onExportProject() {
		String title = appLang.getText("action.project.export");
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(appLang.getText("dialog.fileChooser.extension.svg"), "*.svg");
		File location = dialogUtils.showFileChooser(FileChooserAction.SAVE_DIALOG, title, filter);
		if (location != null) {
			try {
				workspaceActionExecutor.exportProjectAsSvg(location);
			} catch (ProjectIOException e) {
				logger.log(Level.SEVERE, "Failed exporting project at '" + location.getAbsolutePath() + "'", e);
				showErrorDialog(title);
			}
		}
	}

	@Override
	public void onWindowCloseRequest(WindowEvent event) {
		if (showConfirmDialog() && !confirmWorkspaceAction(appLang.getText("action.quit"))) {
			event.consume();
		}
	}

	@Override
	public void onExitApp() {
		if (!showConfirmDialog() || confirmWorkspaceAction(appLang.getText("action.quit"))) {
			Platform.exit();
		}
	}

	private void openProject(File location, String errorTitle) {
		try {
			workspaceActionExecutor.openProject(location);
			recentProjectManager.addRecentProject(location);
		} catch (ProjectIOException e) {
			logger.log(Level.SEVERE, "Failed loading project from '" + location.getAbsolutePath() + "'", e);
			showErrorDialog(errorTitle);
		}
	}

	private boolean showConfirmDialog() {
		return appConfig.getBool("flags.showConfirmDialogs");
	}

	private File showProjectFileChooser(FileChooserAction action) {
		String projectExtension = appConfig.getString("extension.project");
		String extensionTitle = appLang.getText("dialog.fileChooser.extension.project");
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(extensionTitle, "*." + projectExtension);
		return dialogUtils.showFileChooser(action, appLang.getText("action.project." + action.langKey), filter);
	}

	private void saveProject(boolean asNew) {
		File location;
		boolean savingAsNew = asNew || loadState.file.get() == null;
		if (savingAsNew) {
			location = showProjectFileChooser(FileChooserAction.SAVE_DIALOG);
			if (location == null) {
				return;
			}
		} else {
			location = loadState.file.get();
		}
		try {
			location = workspaceActionExecutor.saveProject(location);
			if(savingAsNew)
				recentProjectManager.addRecentProject(location);
		} catch (ProjectIOException e) {
			logger.log(Level.SEVERE, "Failed saving project to '" + location.getAbsolutePath() + "'", e);
			showErrorDialog(appLang.getText("action.project.save"));
		}
	}

	private boolean confirmWorkspaceAction(String title) {
		if (loadState.stateSaved.get()) {
			return true;
		}
		ButtonType saveButton = new ButtonType(appLang.getText("action.save"));
		ButtonType discardButton = new ButtonType(appLang.getText("action.discard"));
		ButtonType cancelButton = new ButtonType(appLang.getText("action.cancel"));
		List<String> headerParts = appLang.getMultipartText("dialog.header.warning.modified");
		String headerText = headerParts.get(0) + getProjectName() + headerParts.get(1);
		String contentText = appLang.getText("dialog.content.warning.modified");
		ButtonType[] buttonTypes = {saveButton, discardButton, cancelButton};
		Optional<ButtonType> response = dialogUtils.showWarningDialog(title, headerText, contentText, buttonTypes);
		if (!response.isPresent() || response.get() == cancelButton) {
			return false;
		}
		if (response.get() == saveButton) {
			onSaveProject();
		}
		return true;
	}

	private void showErrorDialog(String titleText) {
		dialogUtils.showErrorDialog(titleText, appLang.getText("dialog.header.error.workspaceAction"), appLang.getText("dialog.content.error.workspaceAction"));
	}

}
