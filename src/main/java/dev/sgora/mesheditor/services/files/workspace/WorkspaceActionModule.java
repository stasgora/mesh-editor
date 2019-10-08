package dev.sgora.mesheditor.services.files.workspace;

import com.google.inject.AbstractModule;
import dev.sgora.mesheditor.services.files.workspace.interfaces.RecentProjectManager;
import dev.sgora.mesheditor.services.files.workspace.interfaces.WorkspaceAction;

public class WorkspaceActionModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(WorkspaceAction.class).to(WorkspaceActionFacade.class);
		bind(RecentProjectManager.class).to(AppRecentProjectManager.class);
	}
}
