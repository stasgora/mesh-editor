package stasgora.mesh.editor.services.files;

import com.google.inject.AbstractModule;

public class FileIOModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(FileUtils.class).to(ProjectFileUtils.class);
	}
}
