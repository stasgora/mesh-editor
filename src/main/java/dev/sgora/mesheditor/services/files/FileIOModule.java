package dev.sgora.mesheditor.services.files;

import com.google.inject.AbstractModule;

public class FileIOModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(FileUtils.class).to(ProjectFileUtils.class);
	}
}
