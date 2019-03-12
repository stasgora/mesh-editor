package sgora.mesh.editor.view;

import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import sgora.mesh.editor.enums.SubView;
import sgora.mesh.editor.interfaces.files.WorkspaceAction;

public class MenuView extends SubController {

	private WorkspaceAction workspaceAction;

	public MenuItem newProjectMenuItem;
	public MenuItem openProjectMenuItem;
	public MenuItem openRecentMenuItem;
	public MenuItem closeProjectMenuItem;
	public MenuItem saveProjectMenuItem;
	public MenuItem saveProjectAsMenuItem;
	public MenuItem exitAppMenuItem;

	public MenuView(Region root, SubView subView, WorkspaceAction workspaceAction) {
		super(root, subView);
		this.workspaceAction = workspaceAction;
		loadView();
	}

	@Override
	public void init() {
		newProjectMenuItem.setOnAction(event -> workspaceAction.onNewProject());
		openProjectMenuItem.setOnAction(event -> workspaceAction.onOpenProject());
		closeProjectMenuItem.setOnAction(event -> workspaceAction.onCloseProject());
		saveProjectMenuItem.setOnAction(event -> workspaceAction.onSaveProject());
		saveProjectAsMenuItem.setOnAction(event -> workspaceAction.onSaveProjectAs());
		exitAppMenuItem.setOnAction(event -> workspaceAction.onExitApp());
	}

}
