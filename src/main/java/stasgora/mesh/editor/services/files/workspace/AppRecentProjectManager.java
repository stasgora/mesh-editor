package stasgora.mesh.editor.services.files.workspace;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import stasgora.mesh.editor.services.config.annotation.AppConfig;
import stasgora.mesh.editor.services.config.annotation.AppSettings;
import stasgora.mesh.editor.services.config.interfaces.AppConfigManager;
import stasgora.mesh.editor.services.config.interfaces.AppConfigReader;
import stasgora.mesh.editor.services.files.workspace.interfaces.RecentProjectManager;

import java.io.File;
import java.util.List;

@Singleton
public class AppRecentProjectManager implements RecentProjectManager {
	private static final String RECENT_PROJECTS_KEY = "last.projects";

	private AppConfigManager appSettings;
	private AppConfigReader appConfig;

	@Inject
	AppRecentProjectManager(@AppSettings AppConfigManager appSettings, @AppConfig AppConfigReader appConfig) {
		this.appSettings = appSettings;
		this.appConfig = appConfig;
	}

	@Override
	public void addRecentProject(File location) {
		List<String> projects = appSettings.getStringList(RECENT_PROJECTS_KEY);
		String newLocation = location.getAbsolutePath();
		if(projects.contains(newLocation))
			projects.remove(newLocation);
		projects.add(0, newLocation);
		int maxSize = appConfig.getInt("app.recentProjectCount");
		if(projects.size() > maxSize)
			projects = projects.subList(0, maxSize);
		appSettings.setStringList(RECENT_PROJECTS_KEY, projects);
	}
}
