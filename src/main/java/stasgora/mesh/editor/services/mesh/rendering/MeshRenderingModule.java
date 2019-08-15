package stasgora.mesh.editor.services.mesh.rendering;

import com.google.inject.AbstractModule;

public class MeshRenderingModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(SvgMeshRenderer.class).to(JFreeSvgMeshRenderer.class);
		bind(CanvasRenderer.class).to(CanvasMeshRenderer.class);
	}
}
