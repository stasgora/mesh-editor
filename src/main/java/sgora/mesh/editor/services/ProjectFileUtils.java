package sgora.mesh.editor.services;

import javafx.scene.image.Image;
import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.containers.Model;
import sgora.mesh.editor.model.containers.ProjectModel;
import sgora.mesh.editor.model.geom.Mesh;

import java.io.*;

public class ProjectFileUtils implements FileUtils {

	public static final String PROJECT_FILE_EXTENSION = "mesh";
	public static final String DEFAULT_PROJECT_FILE_NAME = "Untitled";

	@Override
	public void save(ProjectModel model, File location) throws ProjectIOException {
		try {
			location.createNewFile();
			try(FileOutputStream fileStream = new FileOutputStream(location, false);
			    ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
				objectStream.writeObject(model.mesh.get());
				fileStream.write(model.rawImageFile);
			}
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void load(ProjectModel model, File location) throws ProjectIOException {
		try(FileInputStream fileStream = new FileInputStream(location);
		    ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
			model.mesh.set((Mesh) objectStream.readObject());
			loadImage(model, fileStream);
		} catch (IOException | ClassNotFoundException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void loadImage(ProjectModel model, FileInputStream fileStream) throws ProjectIOException {
		model.rawImageFile = readFileIntoMemory(fileStream);
		try(ByteArrayInputStream imageStream = new ByteArrayInputStream(model.rawImageFile)) {
			model.baseImage.set(new Image(imageStream));
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public byte[] readFileIntoMemory(FileInputStream fileStream) throws ProjectIOException {
		try(ByteArrayOutputStream imageStream = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[4096];
			int read;
			while ((read = fileStream.read(buffer)) != -1) {
				imageStream.write(buffer, 0, read);
			}
			return imageStream.toByteArray();
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public File getProjectFileWithExtension(File projectFile) {
		String projectExtension = "." + PROJECT_FILE_EXTENSION;
		if(!projectFile.getName().endsWith(projectExtension))
			return new File(projectFile.getPath() + projectExtension);
		return projectFile;
	}

	@Override
	public void setProjectFileName(ProjectModel model) {
		if(model.file.get() == null) {
			model.name.set(model.loaded.get() ? ProjectFileUtils.DEFAULT_PROJECT_FILE_NAME : null);
		} else {
			String fileName = model.file.get().getName();
			model.name.set(fileName.substring(0, fileName.length() - PROJECT_FILE_EXTENSION.length() - 1));
		}
	}

}
