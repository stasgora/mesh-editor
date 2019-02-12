package sgora.mesh.editor.services;

import javafx.scene.image.Image;
import sgora.mesh.editor.State;
import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.geom.Mesh;

import java.io.*;

public class ProjectFileUtils implements FileUtils {

	public static final String DEFAULT_PROJECT_FILE_NAME = "Untitled";
	
	private State state;

	public ProjectFileUtils(State state) {
		this.state = state;
	}

	@Override
	public void save(File location) throws ProjectIOException {
		try {
			location.createNewFile();
			try(FileOutputStream fileStream = new FileOutputStream(location, false);
			    ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
				objectStream.writeObject(state.model.project.mesh.get());
				fileStream.write(state.model.project.rawImageFile);
			}
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void load(File location) throws ProjectIOException {
		try(FileInputStream fileStream = new FileInputStream(location);
		    ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
			state.model.project.mesh.set((Mesh) objectStream.readObject());
			loadImage(fileStream);
		} catch (IOException | ClassNotFoundException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void loadImage(FileInputStream fileStream) throws ProjectIOException {
		state.model.project.rawImageFile = readFileIntoMemory(fileStream);
		try(ByteArrayInputStream imageStream = new ByteArrayInputStream(state.model.project.rawImageFile)) {
			state.model.project.baseImage.set(new Image(imageStream));
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
		String projectExtension = "." + state.config.appConfig.<String>getValue("projectExtension");
		if(!projectFile.getName().endsWith(projectExtension))
			return new File(projectFile.getPath() + projectExtension);
		return projectFile;
	}

}
