package sgora.mesh.editor.interfaces;

import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.model.containers.ProjectModel;

import java.io.File;
import java.io.FileInputStream;

public interface FileUtils {

	void save(ProjectModel model, File location) throws ProjectIOException;

	void load(ProjectModel model, File location) throws ProjectIOException;

	void loadImage(ProjectModel model, FileInputStream fileStream) throws ProjectIOException;

	byte[] readFileIntoMemory(FileInputStream fileStream) throws ProjectIOException;

	File getProjectFileWithExtension(File projectFile);

	void setProjectFileName(ProjectModel model);

}
