package stasgora.mesh.editor.services.input;

import com.google.inject.AbstractModule;

public class InputModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(CanvasAction.class).to(CanvasActionFacade.class);
	}
}
