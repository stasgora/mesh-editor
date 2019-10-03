package stasgora.mesh.editor.services.files.workspace;

import com.google.inject.AbstractModule;
import stasgora.mesh.editor.services.files.workspace.interfaces.RecentProjectManager;
import stasgora.mesh.editor.services.files.workspace.interfaces.WorkspaceAction;

public class WorkspaceActionModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(WorkspaceAction.class).to(WorkspaceActionFacade.class);
		bind(RecentProjectManager.class).to(AppRecentProjectManager.class);
	}
}
