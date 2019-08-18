package stasgora.mesh.editor.view;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import stasgora.mesh.editor.model.NamespaceMap;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.files.workspace.WorkspaceAction;
import stasgora.mesh.editor.services.history.ActionHistoryService;
import stasgora.mesh.editor.view.annotation.MainWindowStage;
import stasgora.mesh.editor.view.sub.SubView;

import java.nio.file.Paths;

public class MenuView extends SubView {

	private final Stage stage;
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

	public MenuItem reloadStylesMenuItem;

	private static final String MENU_FILE_ITEM_DISABLED = "menu_file_item_disabled";

	@Inject
	MenuView(@Assisted Region root, @Assisted ViewType viewType, NamespaceMap viewNamespaces, @MainWindowStage Stage stage,
	         WorkspaceAction workspaceAction, LoadState loadState, ActionHistoryService actionHistoryService, AboutWindow aboutWindow) {
		super(root, viewType, viewNamespaces);
		this.stage = stage;
		this.workspaceAction = workspaceAction;
		this.loadState = loadState;
		this.actionHistoryService = actionHistoryService;
		this.aboutWindow = aboutWindow;
		init();
	}

	@Override
	protected void init() {
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

		reloadStylesMenuItem.setOnAction(event -> {
			ObservableList<String> stylesheets = stage.getScene().getStylesheets();
			stylesheets.clear();
			stylesheets.add(Paths.get("src/main/resources/styles/dark.css").toUri().toString());
		});

		namespace.put(MENU_FILE_ITEM_DISABLED, true);
		loadState.loaded.addListener(() -> namespace.put(MENU_FILE_ITEM_DISABLED, !((boolean) namespace.get(MENU_FILE_ITEM_DISABLED))));
	}

}
