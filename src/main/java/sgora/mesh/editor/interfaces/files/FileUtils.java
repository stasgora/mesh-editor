package sgora.mesh.editor.interfaces.files;

import sgora.mesh.editor.exceptions.ProjectIOException;

import java.io.File;
import java.io.FileInputStream;

public interface FileUtils {

	void save(File location) throws ProjectIOException;

	void load(File location) throws ProjectIOException;

	void loadImage(FileInputStream fileStream) throws ProjectIOException;

	byte[] readFileIntoMemory(FileInputStream fileStream) throws ProjectIOException;

	File getProjectFileWithExtension(File projectFile);

}
