package dev.sgora.mesheditor.services.files.workspace;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.sgora.mesheditor.model.project.LoadState;
import dev.sgora.mesheditor.services.config.annotation.AppConfig;
import dev.sgora.mesheditor.services.config.annotation.AppSettings;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigManager;
import dev.sgora.mesheditor.services.files.workspace.interfaces.RecentProjectManager;
import dev.sgora.mesheditor.services.config.interfaces.AppConfigReader;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AppRecentProjectManager implements RecentProjectManager {
	private static final String RECENT_PROJECTS_KEY = "last.projects";

	private AppConfigManager appSettings;
	private AppConfigReader appConfig;
	private LoadState loadState;

	@Inject
	AppRecentProjectManager(@AppSettings AppConfigManager appSettings, @AppConfig AppConfigReader appConfig, LoadState loadState) {
		this.appSettings = appSettings;
		this.appConfig = appConfig;
		this.loadState = loadState;

		loadRecentProjects();
	}

	@Override
	public void addRecentProject(File location) {
		List<File> projects = loadState.recentProjects.get();
		location = location.getAbsoluteFile();
		projects.remove(location);
		projects.add(0, location);
		int maxSize = appConfig.getInt("app.recentProjectCount");
		if(projects.size() > maxSize)
			projects = projects.subList(0, maxSize);
		loadState.recentProjects.setAndNotify(projects);
		appSettings.setStringList(RECENT_PROJECTS_KEY, projects.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
	}

	private void loadRecentProjects() {
		List<String> projects = appSettings.getStringList(RECENT_PROJECTS_KEY);
		loadState.recentProjects.set(projects.stream().map(File::new).collect(Collectors.toList()));
	}
}
