package stasgora.mesh.editor.services.mesh.generation;

import com.google.inject.AbstractModule;

public class MeshGenerationModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(TriangulationService.class).to(FlipBasedTriangulationService.class);
	}
}
