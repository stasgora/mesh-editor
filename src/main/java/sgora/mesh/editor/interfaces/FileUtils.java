package sgora.mesh.editor.interfaces;

import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.model.containers.ProjectModel;

import java.io.File;
import java.io.FileInputStream;

public interface FileUtils {

	void save(File location) throws ProjectIOException;

	void load(File location) throws ProjectIOException;

	void loadImage(FileInputStream fileStream) throws ProjectIOException;

	byte[] readFileIntoMemory(FileInputStream fileStream) throws ProjectIOException;

	File getProjectFileWithExtension(File projectFile);

}
