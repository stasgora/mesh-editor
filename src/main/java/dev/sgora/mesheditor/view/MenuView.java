package dev.sgora.mesheditor.view;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import dev.sgora.mesheditor.model.project.LoadState;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import dev.sgora.mesheditor.model.NamespaceMap;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigReader;
import dev.sgora.mesheditor.services.config.annotation.AppConfig;
import dev.sgora.mesheditor.services.files.FileUtils;
import dev.sgora.mesheditor.services.files.workspace.interfaces.WorkspaceAction;
import dev.sgora.mesheditor.services.history.ActionHistoryService;
import dev.sgora.mesheditor.view.annotation.MainWindowStage;
import dev.sgora.mesheditor.view.sub.SubView;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class MenuView extends SubView {
	@FXML
	private MenuItem newProjectMenuItem;
	@FXML
	private MenuItem openProjectMenuItem;
	@FXML
	private Menu openRecentMenu;
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
	private final FileUtils fileUtils;
	private final LoadState loadState;

	private static final String MENU_FILE_ITEM_DISABLED = "menu_file_item_disabled";
	private static final String DEBUG_MENU_VISIBLE = "debug_menu_visible";

	@Inject
	MenuView(@Assisted Region root, @Assisted ViewType viewType, NamespaceMap viewNamespaces, @MainWindowStage Stage stage, @AppConfig AppConfigReader appConfig,
	         WorkspaceAction workspaceAction, LoadState loadState, ActionHistoryService actionHistoryService, AboutWindow aboutWindow, FileUtils fileUtils) {
		super(root, viewType, viewNamespaces);
		this.stage = stage;
		this.appConfig = appConfig;
		this.workspaceAction = workspaceAction;
		this.loadState = loadState;
		this.actionHistoryService = actionHistoryService;
		this.aboutWindow = aboutWindow;
		this.fileUtils = fileUtils;
		init();
	}

	@Override
	protected void init() {
		bindMenuItems();

		namespace.put(MENU_FILE_ITEM_DISABLED, true);
		namespace.put(DEBUG_MENU_VISIBLE, appConfig.getBool("app.debugMode"));
		loadState.loaded.addListener(() -> namespace.put(MENU_FILE_ITEM_DISABLED, !((boolean) namespace.get(MENU_FILE_ITEM_DISABLED))));

		loadState.recentProjects.addListener(this::onRecentProjectsChanged);
		onRecentProjectsChanged();
	}

	private void onRecentProjectsChanged() {
		ObservableList<MenuItem> items = openRecentMenu.getItems();
		items.clear();
		List<File> recentProjects = loadState.recentProjects.get();
		if(!recentProjects.isEmpty()) {
			items.addAll(recentProjects.stream().map(this::createRecentProjectMenuItem).collect(Collectors.toList()));
		}
		openRecentMenu.setDisable(recentProjects.isEmpty());
	}

	private MenuItem createRecentProjectMenuItem(File projectFile) {
		MenuItem item = new MenuItem(fileUtils.getProjectFileName(projectFile));
		item.setOnAction(event -> workspaceAction.onOpenRecentProject(projectFile));
		return item;
	}

	private void bindMenuItems() {
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
	}

}
