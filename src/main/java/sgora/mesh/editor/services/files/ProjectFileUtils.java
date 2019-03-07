package sgora.mesh.editor.services.files;

import javafx.scene.image.Image;
import sgora.mesh.editor.exceptions.ProjectIOException;
import sgora.mesh.editor.interfaces.config.AppConfigReader;
import sgora.mesh.editor.interfaces.FileUtils;
import sgora.mesh.editor.model.observables.SettableObservable;
import sgora.mesh.editor.model.project.ProjectState;
import sgora.mesh.editor.model.project.VisualProperties;
import sgora.mesh.editor.model.geom.Mesh;

import java.io.*;

public class ProjectFileUtils implements FileUtils {
	
	private ProjectState projectState;
	private AppConfigReader appConfig;
	private SettableObservable<VisualProperties> visualProperties;

	public ProjectFileUtils(ProjectState projectState, AppConfigReader appConfig, SettableObservable<VisualProperties> visualProperties) {
		this.projectState = projectState;
		this.appConfig = appConfig;
		this.visualProperties = visualProperties;
	}

	@Override
	public void save(File location) throws ProjectIOException {
		try {
			location.createNewFile();
			try(FileOutputStream fileStream = new FileOutputStream(location, false);
			    ObjectOutputStream objectStream = new ObjectOutputStream(fileStream)) {
				objectStream.writeObject(projectState.mesh.get());
				objectStream.writeObject(visualProperties.get());
				fileStream.write(projectState.rawImageFile);
			}
		} catch (IOException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void load(File location) throws ProjectIOException {
		try(FileInputStream fileStream = new FileInputStream(location);
		    ObjectInputStream objectStream = new ObjectInputStream(fileStream)) {
			projectState.mesh.set((Mesh) objectStream.readObject());
			visualProperties.set((VisualProperties) objectStream.readObject());
			loadImage(fileStream);
		} catch (IOException | ClassNotFoundException e) {
			throw new ProjectIOException(e);
		}
	}

	@Override
	public void loadImage(FileInputStream fileStream) throws ProjectIOException {
		projectState.rawImageFile = readFileIntoMemory(fileStream);
		try(ByteArrayInputStream imageStream = new ByteArrayInputStream(projectState.rawImageFile)) {
			projectState.baseImage.set(new Image(imageStream));
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
		String projectExtension = "." + appConfig.getString("extension.project");
		if(!projectFile.getName().endsWith(projectExtension)) {
			return new File(projectFile.getPath() + projectExtension);
		}
		return projectFile;
	}

}
