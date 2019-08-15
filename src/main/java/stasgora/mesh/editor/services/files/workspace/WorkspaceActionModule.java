package stasgora.mesh.editor.services.files.workspace;

import com.google.inject.AbstractModule;

public class WorkspaceActionModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(WorkspaceAction.class).to(WorkspaceActionFacade.class);
	}
}
