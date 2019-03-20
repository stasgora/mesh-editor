package sgora.mesh.editor.services.files;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import sgora.mesh.editor.enums.FileChooserAction;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.interfaces.config.LangConfigReader;
import sgora.mesh.editor.interfaces.files.WorkspaceAction;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.LoadState;
import sgora.mesh.editor.services.UiDialogUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class WorkspaceActionFacade implements WorkspaceAction {

	private final WorkspaceActionExecutor workspaceActionExecutor;
	private final LangConfigReader appLang;
	private UiDialogUtils dialogUtils;
	private AppConfigReader appConfig;
	private LoadState loadState;

	public WorkspaceActionFacade(WorkspaceActionExecutor workspaceActionExecutor, LangConfigReader appLang,
	                             UiDialogUtils dialogUtils, AppConfigReader appConfig, LoadState loadState) {
		this.workspaceActionExecutor = workspaceActionExecutor;
		this.appLang = appLang;
		this.dialogUtils = dialogUtils;
		this.appConfig = appConfig;
		this.loadState = loadState;
	}

	@Override
	public String getProjectName() {
		String projectName;
		LoadState loadState = this.loadState;
		if(loadState.file.get() == null) {
			projectName = loadState.loaded.get() ? appLang.getText("defaultProjectName") : null;
		} else {
			String fileName = loadState.file.get().getName();
			projectName = fileName.substring(0, fileName.length() - appConfig.getString("extension.project").length() - 1);
		}
		return projectName;
	}

	@Override
	public void onNewProject() {
		if(showConfirmDialog() && !confirmWorkspaceAction(appLang.getText("action.createProject"))) {
			return;
		}
		String[] imageTypes = appConfig.getStringList("supported.imageTypes").stream().map(item -> "*." + item).toArray(String[]::new);
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(appLang.getText("dialog.fileChooser.extension.image"), imageTypes);
		File location = dialogUtils.showFileChooser(FileChooserAction.OPEN_DIALOG, appLang.getText("dialog.fileChooser.title.image"), filter);
		if(location != null) {
			workspaceActionExecutor.createNewProject(location);
		}
	}

	@Override
	public void onOpenProject() {
		if(showConfirmDialog() && !confirmWorkspaceAction(appLang.getText("action.openProject"))) {
			return;
		}
		File location = showProjectFileChooser(FileChooserAction.OPEN_DIALOG);
		if(location != null) {
			workspaceActionExecutor.openProject(location);
		}
	}

	@Override
	public void onOpenRecentProject() {

	}

	@Override
	public void onCloseProject() {
		if(!showConfirmDialog() || confirmWorkspaceAction(appLang.getText("action.closeProject"))) {
			workspaceActionExecutor.closeProject();
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
	public void onWindowCloseRequest(WindowEvent event) {
		if(showConfirmDialog() && !confirmWorkspaceAction(appLang.getText("action.quit"))) {
			event.consume();
		}
	}

	@Override
	public void onExitApp() {
		if(!showConfirmDialog() || confirmWorkspaceAction(appLang.getText("action.quit"))) {
			Platform.exit();
		}
	}

	private boolean showConfirmDialog() {
		return appConfig.getBool("flags.showConfirmDialogs");
	}

	private File showProjectFileChooser(FileChooserAction action) {
		String projectExtension = appConfig.getString("extension.project");
		String extensionTitle = appLang.getText("dialog.fileChooser.extension.project");
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(extensionTitle, "*." + projectExtension);
		return dialogUtils.showFileChooser(action, appLang.getText("dialog.fileChooser.title.project"), filter);
	}

	private void saveProject(boolean asNew) {
		File location;
		LoadState loadState = this.loadState;
		if(asNew || loadState.file.get() == null) {
			location = showProjectFileChooser(FileChooserAction.SAVE_DIALOG);
			if(location == null) {
				return;
			}
		} else {
			location = loadState.file.get();
		}
		workspaceActionExecutor.saveProject(location);
	}

	private boolean confirmWorkspaceAction(String title) {
		if(loadState.stateSaved.get()) {
			return true;
		}
		ButtonType saveButton = new ButtonType(appLang.getText("action.save"));
		ButtonType discardButton = new ButtonType(appLang.getText("action.discard"));
		ButtonType cancelButton = new ButtonType(appLang.getText("action.cancel"));
		List<String> headerParts = appLang.getMultipartText("dialog.warning.header.modified");
		String headerText = headerParts.get(0) + getProjectName() + headerParts.get(1);
		String contentText = appLang.getText("dialog.warning.content.modified");
		ButtonType[] buttonTypes = {saveButton, discardButton, cancelButton};
		Optional<ButtonType> response = dialogUtils.showWarningDialog(title, headerText, contentText, buttonTypes);
		if(!response.isPresent() || response.get() == cancelButton) {
			return false;
		}
		if(response.get() == saveButton) {
			onSaveProject();
		}
		return true;
	}

}
