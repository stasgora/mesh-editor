package stasgora.mesh.editor.view;

import javafx.collections.ObservableMap;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.files.workspace.WorkspaceAction;
import stasgora.mesh.editor.services.history.ActionHistoryService;

import java.util.Map;

public class MenuView extends SubController {

	private WorkspaceAction workspaceAction;
	private final ActionHistoryService actionHistoryService;
	private final AboutWindow aboutWindow;
	private final LoadState loadState;

	public MenuItem newProjectMenuItem;
	public MenuItem openProjectMenuItem;
	public MenuItem openRecentMenuItem;
	public MenuItem closeProjectMenuItem;
	public MenuItem saveProjectMenuItem;
	public MenuItem saveProjectAsMenuItem;
	public MenuItem exportProjectMenuItem;
	public MenuItem exitAppMenuItem;

	public MenuItem undoMenuItem;
	public MenuItem redoMenuItem;

	public MenuItem aboutMenuItem;

	private static final String MENU_FILE_ITEM_DISABLED = "menu_file_item_disabled";

	public MenuView(Region root, ViewType viewType, Map<String, ObservableMap<String, Object>> viewNamespaces,
	                WorkspaceAction workspaceAction, LoadState loadState, ActionHistoryService actionHistoryService, AboutWindow aboutWindow) {
		super(root, viewType, viewNamespaces);
		this.workspaceAction = workspaceAction;
		this.loadState = loadState;
		this.actionHistoryService = actionHistoryService;
		this.aboutWindow = aboutWindow;
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

		undoMenuItem.setOnAction(event -> actionHistoryService.undo());
		redoMenuItem.setOnAction(event -> actionHistoryService.redo());

		aboutMenuItem.setOnAction(event -> aboutWindow.show());

		namespace.put(MENU_FILE_ITEM_DISABLED, true);
		loadState.loaded.addListener(() -> namespace.put(MENU_FILE_ITEM_DISABLED, !((boolean) namespace.get(MENU_FILE_ITEM_DISABLED))));
	}

}
