package stasgora.mesh.editor.view;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import stasgora.mesh.editor.model.NamespaceMap;
import stasgora.mesh.editor.model.project.LoadState;
import stasgora.mesh.editor.services.config.interfaces.AppConfigReader;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.files.workspace.interfaces.WorkspaceAction;
import stasgora.mesh.editor.services.history.ActionHistoryService;
import stasgora.mesh.editor.view.annotation.MainWindowStage;
import stasgora.mesh.editor.view.sub.SubView;

import java.nio.file.Paths;

public class MenuView extends SubView {
	@FXML
	private MenuItem newProjectMenuItem;
	@FXML
	private MenuItem openProjectMenuItem;
	@FXML
	private MenuItem openRecentMenuItem;
	@FXML
	private MenuItem closeProjectMenuItem;
	@FXML
	private MenuItem saveProjectMenuItem;
	@FXML
	private MenuItem saveProjectAsMenuItem;
	@FXML
	private MenuItem exportProjectMenuItem;
	@FXML
	private MenuItem exitAppMenuItem;

	@FXML
	private MenuItem undoMenuItem;
	@FXML
	private MenuItem redoMenuItem;

	@FXML
	private MenuItem aboutMenuItem;

	@FXML
	private MenuItem reloadStylesMenuItem;

	private final Stage stage;
	private final AppConfigReader appConfig;
	private WorkspaceAction workspaceAction;
	private final ActionHistoryService actionHistoryService;
	private final AboutWindow aboutWindow;
	private final LoadState loadState;

	private static final String MENU_FILE_ITEM_DISABLED = "menu_file_item_disabled";
	private static final String DEBUG_MENU_VISIBLE = "debug_menu_visible";

	@Inject
	MenuView(@Assisted Region root, @Assisted ViewType viewType, NamespaceMap viewNamespaces, @MainWindowStage Stage stage, @AppConfig AppConfigReader appConfig,
	         WorkspaceAction workspaceAction, LoadState loadState, ActionHistoryService actionHistoryService, AboutWindow aboutWindow) {
		super(root, viewType, viewNamespaces);
		this.stage = stage;
		this.appConfig = appConfig;
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
		namespace.put(DEBUG_MENU_VISIBLE, appConfig.getBool("app.debugMode"));
		loadState.loaded.addListener(() -> namespace.put(MENU_FILE_ITEM_DISABLED, !((boolean) namespace.get(MENU_FILE_ITEM_DISABLED))));
	}

}
