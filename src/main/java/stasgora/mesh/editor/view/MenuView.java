package stasgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.enums.ViewType;
import stasgora.mesh.editor.interfaces.files.WorkspaceAction;
import stasgora.mesh.editor.model.project.LoadState;

import java.util.Map;

public class MenuView extends SubController {

	private WorkspaceAction workspaceAction;
	private final LoadState loadState;

	public MenuItem newProjectMenuItem;
	public MenuItem openProjectMenuItem;
	public MenuItem openRecentMenuItem;
	public MenuItem closeProjectMenuItem;
	public MenuItem saveProjectMenuItem;
	public MenuItem saveProjectAsMenuItem;
	public MenuItem exportProjectMenuItem;
	public MenuItem exitAppMenuItem;

	private static final String MENU_FILE_ITEM_DISABLED = "menu_file_item_disabled";

	public MenuView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces,
	                WorkspaceAction workspaceAction, LoadState loadState) {
		super(root, viewType, viewNamespaces);
		this.workspaceAction = workspaceAction;
		this.loadState = loadState;
		init();
	}

	@Override
	public void init() {
		newProjectMenuItem.setOnAction(event -> workspaceAction.onNewProject());
		openProjectMenuItem.setOnAction(event -> workspaceAction.onOpenProject());
		closeProjectMenuItem.setOnAction(event -> workspaceAction.onCloseProject());
		saveProjectMenuItem.setOnAction(event -> workspaceAction.onSaveProject());
		saveProjectAsMenuItem.setOnAction(event -> workspaceAction.onSaveProjectAs());
		exportProjectMenuItem.setOnAction(event -> workspaceAction.onExportProject());
		exitAppMenuItem.setOnAction(event -> workspaceAction.onExitApp());

		namespace.put(MENU_FILE_ITEM_DISABLED, true);
		loadState.loaded.addListener(() -> namespace.put(MENU_FILE_ITEM_DISABLED, !((boolean) namespace.get(MENU_FILE_ITEM_DISABLED))));
	}

}
